package textModule;

import java.io.IOException;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;

import Algorithm.CraftML;
import FilesManagement.SmallItem;
import utils.MessManager;
import utils.PerformanceEvaluator;


public class CraftML4Text_API {

	static final String modelExtensionName=".craftTXT.txt";


	//========> TODO : IL FAUDRA AJOUTER UN MODE LEARN "BIG DATAFILE"  <==========

	public String modelForTextExtension=".4TEXT.txt"; 

	public CraftML myModel;  // Model that will be used

	public CraftML4Text_Params myParams= new CraftML4Text_Params();  //Params that will be used  for craftml AND for parsing ; By default use default paramerters

	// ========== Online oriented functions  : LEARNING, PARSING UTILITIES, PREDICTION ===========

	public SmallParser myParser= new SmallParser(); // mini-parser for text-to-SmallItem operations  // IDEM BY DEFAULT USE DEFAULT PARAMETERS



	long numberOfExample=0;
	long numberOfTrainingExample=0;
	long numberOfEmptyExample=0;
	long numberOfCommentary=0;
	long numberOfNullExample=0;
	long numberOfSyntaxicallyWrongExample=0;


	// ----- life cycle for building a new model -----

	/**
	 * prepare a new model, using the parameters defined in the CraftMLText_Params Class
	 * the parameters (from an instance of Cratml4textParams are linked)
	 * Will init also the Parser for the text, using the parameters in the CraftMLText_Parameters Class
	 * @return message OK or error message
	 */
	public String newModel() {

		// ----  Params : TO SEE : parameters VIA function call

		CraftML4Text_Params params=myParams;

		// -- prep model CraftML

		myModel =  new CraftML();
		myModel.setNbTrees(params.numberOfTrees);
		myModel.setMode("DXDY");
		myModel.depthMax=params.depthMax;
		myModel.setMinInst(params.minInstanceInLeaf);
		//myModel.branchFactor=branchFactor;
		myModel.setBranchFactor(params.branchFactor);
		myModel.setDimReductionX(params.xProjectionSize);
		myModel.setDimReductionY(params.yProjectionSize);
		myModel.sparsity=params.sparsity;
		myModel.sizeReservoirKmeans=params.sizeReservoirSampling4Kmeans;
		//
		myModel.nbThread=4;
		myModel.setOptimizeMemory(params.optimiseMemory);
		myModel.setAllTreesTogether(params.allTreesTogether);
		myModel.reinitialize();

		resetPassOnFileStatus();

		//---- Text Parser ----

		myParser=new SmallParser();

		//  parser initialization
		myParser.caseSensitive=myParams.caseSentitive;
		myParser.deletePunctuation=myParams.removePunctuation;
		myParser.letterNgramSize=myParams.maxCharNgram;
		myParser.wordNgramSize=myParams.maxWordNgram;


		return "model -and parser- initialized";
	}


	/**
	 * Adding an example, directly in raw text (will be parsed)
	 * @param example: must respects the syntax   raw text  => list of label 
	 * @return message OK or error message
	 */
	public String addTrainingExample_XYRawText(String example) {
		//System.out.println("addTrainingExampleRawText: receiving: "+example);
		numberOfExample++;
		if (example==null) {
			numberOfEmptyExample++;
			return "error: null text example";
		}
		example=example.trim();
		if (example.equals("")) {
			numberOfEmptyExample++;
			return "warning: empty text example (of full blank)";
		}
		//System.out.println("current example :"+example);
		if (isAcommentaryLine(example)) {
			numberOfCommentary++;
			return "commentary skipped";
		}
		// --- now must be an example ---
		if (!couldBeATrainingOrConstrainedOrTestLine(example)) {
			numberOfSyntaxicallyWrongExample++;
			return "not an x=>y example: "+example;
		}
		numberOfTrainingExample++;

		String[] xy=example.split(myParams.xySep);
		String mess=addTrainingExample_XText_YTextAsListOfLabels(xy[0], xy[1]);

		return mess; 
	}


	/**
	 * Adding an example, the X part in raw text (full text to be parsed) and the Y part in the form of a raw text (list of labels)
	 * Only the X part raw text will be parsed
	 * @param rawText
	 * @param listofExample
	 * @return message OK or error message
	 */
	public String addTrainingExample_XText_YTextAsListOfLabels(String rawTextX, String rawTextY) {
		//System.out.println("add training raw x: "+rawTextX);
		//System.out.println("add traning raw y: "+rawTextY);

		if (rawTextX==null) {
			return "training example not added: x is null";
		}
		if (rawTextY==null) {
			return "training example not added: y is null";
		}

		rawTextX=rawTextX.trim();
		rawTextY=rawTextY.trim();

		SmallItem sx=parseXText(rawTextX);
		String[] ay=parseYLabelsAsSimpleList(rawTextY);
		if (ay==null) {
			return "training example not added: y is null";
		}
		SmallItem sy=myParser.convertArrayToSmallItem(ay);
		addTrainingExample_XYSmallItem(sx, sy);

		return "training example rawTextX rawTextY: added"; 
	}

	/**
	 * Adding  an example, directly in the form of a couple of an explanatory SmallItem X and a target SmallItem Y
	 * No text parsing required
	 * @param x  SmallItem: list of (key, value) couples for the X (explanatory) part
	 * @param y  SmallItem: list of (key, value) couples for the Y (target, labels) part
	 * @return message OK or error message
	 */
	public String addTrainingExample_XYSmallItem(SmallItem x, SmallItem y) {

		myModel.storeForLearning(x, y);
		return "couple (x,y) stored for learning"; 
	}

	/**
	 * compute the model so it will be available in RAM
	 * @return message OK or error message
	 */
	//public String computeModel____() {
	//	
	//}

	// -----------  parsing utilities ----------


	/**
	 * parse a text as a X part (explanatory, left part)
	 * @param x
	 * @return SmallItem, according to the parameters of the parser 
	 */
	public SmallItem parseXText(String x) {
		//System.out.println("va parser en x: "+x);
		SmallItem sx=myParser.getNgramParsing(x);
		return sx;  
	}


	/**
	 * parse a text as a Y part, only the list of labels is expected (without values)
	 * use separators for list of labels  ; and space is the default separator
	 * RETURN NULL if there is no label parsable
	 * @param x
	 * @return
	 */
	public String[] parseYLabelsAsSimpleList(String x) {
		if (x==null) {
			return null;
		}
		x=x.trim();
		if (x.equals("")) {
			return null;
		}
		for (int i=0; i<myParams.labelSeparator.length;i++) {
			x=x.replace(myParams.labelSeparator[i], " ");   
		}
		x=myParser.getNormalizedStringWithoutDoubleSpace(x);
		//System.out.println("VUE LABELS :"+x);
		String[] result=x.split(" ");
		return result; 
	}






	/**
	 * convert an array of labels into a SmallItem (couples of (key, value) ) 
	 * @param labels
	 * @return
	 */
	public SmallItem arrayOflabels2SmallItem(String[] labels) {
		if (labels==null) {
			return null;
		}
		/*  may be not necessary
		if (labels.length==0) {
			return null;
		}
		 */
		SmallItem result= new SmallItem();
		for (int i=0;i <labels.length;i++) {
			result.putKeyValue(labels[i], 1);
		}
		return result;


	}



	/**
	 * indicates if the line is a commentary (in this case, it must be ignored for training/prediction and just copied for the output)
	 * @param line
	 * @return
	 */
	public boolean isAcommentaryLine(String line) {
		assert (line!=null);
		//System.out.println("check if commentary line for line:");
		line=line.trim();
		if (line.startsWith(myParams.commentarySep)) {
			return true;
		}
		return false ; 
	}

	/**
	 * indicates if the line could be a correct training line (or test line, or constrained prediction line) :
	 *  line with a XY separator "=>", with a X part not empty, with a Y part not empty
	 * @param line
	 * @return
	 */
	public boolean couldBeATrainingOrConstrainedOrTestLine(String line) {
		assert (line!=null);
		if (!line.contains(myParams.xySep)) {
			//System.out.println("no prediction sep");
			return false;
		}
		String[] xy=line.split(myParams.xySep);
		if (xy==null) {
			System.out.println("split x => y not possible");
			return false;
		}
		if (xy.length!=2) {
			System.out.println("size of split incorrect (possibly to many '=>' symbols): "+xy.length);
			return false;
		}
		xy[1]=xy[1].trim();
		//if (xy[1])
		return true;
	}

	/**
	 * A PREDICTIVE LINE DOES NOT HAVE THE => SYMBOL, AND IS NOT NULL
	 * @param line
	 * @return
	 */
	public boolean isAToPredicLine(String line) {
		//System.out.println("topred: line: "+line);
		assert(line!=null);
		if (line.contains(myParams.xySep)) {
			//System.out.println("   NO : contains pred sep");
			return false;
		}
		line=line.trim();
		if (line.equals("")) {
			//System.out.println("   NO :  empty");
			return false;
		}
		//System.out.println("   YES ");
		return true;
	}
	
	/**
	 * Makes sure that the training or constrained prediction line contains tabulation only before and after the implication "=>" symbol
	 * @param lineIn, ASSUMED to be a training or contrained prediction lin
	 * @return nomalized line, or the same if pb
	 */
	private String getTrainingOrConstrainedPredictionlineInNormalization(String lineIn) {
		String[] xy=lineIn.split(myParams.xySep);
		if (xy==null) {
			System.out.println("normalization: split x => y not possible");
			return lineIn;
		}
		if (xy.length!=2) {
			System.out.println("normalization : size of split incorrect (possibly to many '=>' symbols): "+xy.length);
			return lineIn;
		}
		xy[0]=xy[0].replaceAll("\t", " ");
		xy[1]=xy[1].replaceAll("\t", " ");
		String lineInNormlized=xy[0]+"\t"+myParams.xySep+"\t"+xy[1];
		lineInNormlized=lineInNormlized.replaceAll(" \t", "\t"); // for no change if the line in was correct
		return lineInNormlized;
	}
	
	/**
	 * Makes sure that the line to predic does not have tabulation
	 * @param lineIn
	 * @return
	 */
	private String getToPredicLineNormalization (String lineIn) {
		String lineInNormalized=lineIn.replaceAll("\t", " ");
		return lineInNormalized;
	}


	//---------------------------------------

	// Utilities

	private String getPassOnFileStatus() {
		String result="";
		result=result+"number of examples: "+numberOfExample+"\n";
		result=result+"number of training examples: "+numberOfTrainingExample+"\n";
		result=result+"number of commentaries: "+numberOfCommentary+"\n";
		result=result+"number of empty Examples: "+numberOfEmptyExample+"\n";
		result=result+"number of examples null (pbs): "+numberOfNullExample+"\n";
		result=result+"number of syntaxically incorrect examples  (pbs): "+numberOfSyntaxicallyWrongExample+"\n";
		return result;
	}

	private void resetPassOnFileStatus() {
		numberOfExample=0;
		numberOfTrainingExample=0;
		numberOfEmptyExample=0;
		numberOfCommentary=0;
		numberOfNullExample=0;
		numberOfSyntaxicallyWrongExample=0;

	}






	// ----------- predictions ------------

	/**
	 * prediction of a sparse vector (SmallItem) Y based on a sparse vector (SmallItem) X given as input
	 * @param x
	 * @return   null if there is a problem (model not available for instance), or a SmallItem with the topN (parameter) most likely labels
	 */
	public SmallItem getYItemPrediction(SmallItem x) {
		assert (x!=null);
		if (myModel==null) {
			MessManager.sayError("prediction Y not possible: no model has been computed");
			return null;
		}
		SmallItem y=myModel.predict(x);
		return y;  
	}
	
	/**
	 * Return only the list of labels for a given minimum probability 
	 * MAY RETURN NULL !
	 * @param x Small Item
	 * @param proba
	 * @return String Array
	 */
	public String[] getOnlyPredictedLabelForProbability(SmallItem x, float proba) {
		SmallItem y=getYItemPrediction(x);
		y.deleteKeyLowerThan(proba);
		String[] result=y.getKeyArray();
		return result;
	}
	
	/**
	 * Get a predicted list of label (only the labels, without the probabilities AS A TEXT, with labelSeparator as separator
	 * @param source: text input for prediction
	 * @param proba: threshold for keeping the labels (greater or equal)
	 * @param labelSeparator
	 * @return
	 */
	public String getOnlyPredictedLabelAsTextForProbability(String source, float proba, String labelSeparator) {
		assert (source!=null);
		SmallItem x=parseXText(source);
		String[] result=getOnlyPredictedLabelForProbability(x, proba);
		if (result==null) {
			return "";
		}
		if (result.length<1) {
			return "";
		}
		String text="";
		for (int i=0;i<result.length;i++) {
			text=text+labelSeparator+result[i];
		}
		return text;
	}
	
	
	/**
	 * Return only the list of labels for a given minimum probability 
	 * MAY RETURN NULL !
	 * @param s String
	 * @param proba
	 * @return String Array
	 */
	public String[] getOnlyPredictedLabelForProbability(String s, float proba) {
		assert (s!=null);
		SmallItem x=parseXText(s);
		//SmallItem y=getYItemPrediction(x);
		//y.deleteKeyLowerThan(proba);
		//String[] result=y.getKeyArray();
		String[] result=getOnlyPredictedLabelForProbability(x, proba);
		return result;
	}

	
	
	public SmallItem getYItemPrediction(String x) {
		assert (x!=null);
		if (myModel==null) {
			MessManager.sayError("prediction Y not possible: no model has been computed");
			return null;
		}
		SmallItem sx=parseXText(x);
		SmallItem y=myModel.predict(sx);
		return y;  
	}

	/**
	 * prediction of the cluster path, based on sparse vector x 
	 * (will use the path of the Tree0 of the forest model)
	 * @param x
	 * @return null if there is a problem (model not available for instance), or the array of the nodeIDs (numbers) of the Tree0, traveled by x
	 */
	public String getClusterPathPrediction(SmallItem x) {
		assert(x!=null);
		if (myModel==null) {
			MessManager.sayError("prediction cluster not possible: no model has been computed");
			return null;
		}
		String s=myModel.getLeaf(x, 0).getID();
		s=s.replace("tree0", "cluster");
		return s;  
	}

	public String getClusterPathPredictionForString(String x)
	{
		assert(x!=null);
		SmallItem sx=parseXText(x);
		String result=getClusterPathPrediction(sx);
		return result;

	}


	/**
	 * prediction in text format of a Y part based on a sparse Vector X part 
	 * @param x
	 * @return
	 */
	public String getYStringPredictionConstrainedOrNot(SmallItem x, String[] listToPredict) {
		String result;
		SmallItem y=myModel.predict(x);
		String[] orderedKeys;
		if (listToPredict==null) {
			orderedKeys=y.getBestKeysDecreasingOrder(myParams.topNLabels);
		} else {
			orderedKeys=listToPredict;
		}

		result=y.getOrderedLinetext(myParams.fieldSeparator,myParams.equalSeparator,orderedKeys);

		//result=y.getLinetext(myParams.fieldSeparator,myParams.equalSeparator);
		return result; 
	}


	public String getYStringPrediction(String Xpart) {
		SmallItem x=myParser.getNgramParsing(Xpart);
		String result=getYStringPredictionConstrainedOrNot(x, null);  // non-constrained prediction
		return result; 
	}

	//public SmallItem getYPredictionFor


	public String getYStringPredictionConstrained(String XPart, String listLabelToPredict) {
		SmallItem x=myParser.getNgramParsing(XPart);
		String[] labelTab=parseYLabelsAsSimpleList(listLabelToPredict);
		String result=getYStringPredictionConstrainedOrNot(x, labelTab);  // constrained prediction
		return result; 
	}



	// ====================   File oriented functions ==================
	//
	//                ATTENTION : CES FONCTIONS NE SONT PAS ORIENTEES "PROCESS ONE SHOT"
	//                TODO : ajouter quelques process "one shot", non dynamiques
	//
	// return message OK or error message


	/**
	 * Create a global model in RAM, using current parameters, and reading a textFile in the X=>Y format used as a TRAIN file,
	 *  X being raw text, Y being a list of labels
	 * @param globalPath
	 * @return message OK or error message
	 */
	public String file_learnModelFromFile(String globalPath) {

		newModel();
		System.out.println("opening train file: "+globalPath);
		SimpleTextReaderUTF8 myInput = new SimpleTextReaderUTF8();
		myInput=new SimpleTextReaderUTF8();
		boolean ok=myInput.openFile(globalPath);
		if (!ok) {
			System.out.println("error trying opening: "+globalPath);
			myModel=null;
			return "error: cannot open: "+globalPath;
		}

		// loop =================================================================

		int lineNumber=0;
		String lineInput=myInput.readLine();
		while (lineInput!=null) {
			lineNumber++;
			System.out.println(lineNumber+" "+lineInput);
			String mess=addTrainingExample_XYRawText(lineInput);
			System.out.println(mess);
			lineInput=myInput.readLine();
		}
		String mess=getPassOnFileStatus();
		myModel.indicationFinDePasse();
		String resultCompteModel= "model computed";
		System.out.println(resultCompteModel);
		//=============================================================================
		myModel.hasLearnt=true;
		return mess ; 
	}





	/**
	 * With the current model in RAM, 
	 *   read a text file in the X=>Y format used as a TEST file 
	 *   and make an evaluation with the precision At K performance indicator 
	 * @param globalPathXY :  File In, the Test file 
	 * @return message OK or error message
	 */
	public String file_eval_precision(String globalPathXY) {

		assert(globalPathXY!=null);
		assert(globalPathXY.length()>0);

		long numberOfNotEvaluatedLine=0;
		long numberOfEvaluatedLine=0;

		SimpleTextReaderUTF8 inputFile= new SimpleTextReaderUTF8();
		SimpleTextWriterUTF8 outputFile=new SimpleTextWriterUTF8();
		SimpleTextWriterUTF8 rapportFile=new SimpleTextWriterUTF8();
		inputFile.openFile(globalPathXY);
		outputFile.openFile(globalPathXY+".evalPrecision.txt");
		rapportFile.openFile(globalPathXY+".rapport.txt");
		rapportFile.writeRecord("test file: "+globalPathXY);
		long t1=System.currentTimeMillis();

		String lineIn=inputFile.readLine();

		PerformanceEvaluator myEval=new PerformanceEvaluator();

		while (lineIn!=null) {

			String lineOut;
			if (isAcommentaryLine(lineIn)) {
				lineOut=lineIn+" ==  identified as commentary";
				lineOut=lineIn;
			} else {

				//lineOut=lineIn+" == not identified "; // DEFAULT
				lineOut=lineIn;
				if (isAToPredicLine(lineIn)) {

					System.out.println("line not evaluated: "+lineIn);
					numberOfNotEvaluatedLine++;

				}
				if (couldBeATrainingOrConstrainedOrTestLine(lineIn)) {  // EVAL MODE
					lineIn=getTrainingOrConstrainedPredictionlineInNormalization(lineIn);
					//String[] xy=lineIn.split(myParams.predictionWriterPrefix);
					String[] xy=lineIn.split(myParams.xySep);
					String predict;   
					String XPart=xy[0];
					String listOflabels=xy[1];
					String[] labelsToPredictOrdered=parseYLabelsAsSimpleList(listOflabels);
					if (labelsToPredictOrdered==null) {
						System.out.println("line not evaluated: "+lineIn);
						numberOfNotEvaluatedLine++;
					} else {
						numberOfEvaluatedLine++;
						SmallItem yPredicted=getYItemPrediction(XPart);
						// à faire : récupérer les labels prédit
						// calculer vrai positifs, faux positifs...
						myEval.addEvalExampleForPrecisionAtK(labelsToPredictOrdered, yPredicted);

						SmallItem x=myParser.getNgramParsing(XPart);

						//predict=getYStringPredictionConstrained(XPart, listOflabels);
						predict=getYStringPredictionConstrainedOrNot(x, null);  // non-constrained prediction

						lineOut=lineIn+myParams.predictionWriterPrefix+predict;
						if (myParams.addClusterInfoPrediction) {
							predict=getClusterPathPredictionForString(XPart);
							lineOut=lineOut+myParams.predictionWriterPrefix+predict;
						}
					}
				}
			}
			outputFile.writeRecord(lineOut);
			lineIn=inputFile.readLine();
		}
		inputFile.closeFile();
		outputFile.closeFile();
		long t2=System.currentTimeMillis();
		float execTimeSeconde=(t2-t1)/1000;

		String s1="File Eval precision, number of line Evaluated: "+numberOfEvaluatedLine;
		String s2="File Eval precision, number of line Not Evaluated: "+numberOfNotEvaluatedLine;
		MessManager.say(s1);
		MessManager.say(s2);
		rapportFile.writeRecord(s1);
		rapportFile.writeRecord(s2);

		String result="Precision@K\n"+myEval.getEvalPrecisionInfo();
		MessManager.say(result);
		rapportFile.writeRecord(result);
		
		//rapportFile.
		rapportFile.writeRecord("Evaluation execution time (secondes): "+execTimeSeconde);
		
		rapportFile.closeFile();
		return result ; 
	}


	


	/**======================  A VOIR =============================
	 * With the current model in RAM, read a text file in the X-only format (no part Y), 
	 *    and replace it with a X=>Y file with the prediction in the y part
	 * if for some lines there is an Y part with labels, 
	 *    it is identified as an constrained prediction (done only for the list of labels of)
	 * @param globalPath  File in, 
	 * @return message OK or error message
	 */
	public String file_predictOnInteractiveFile(String globalPath) {

		assert(globalPath!=null);
		assert(globalPath.length()>0);

		SimpleTextReaderUTF8 inputFile= new SimpleTextReaderUTF8();
		SimpleTextWriterUTF8 outputFile=new SimpleTextWriterUTF8();
		inputFile.openFile(globalPath);
		outputFile.openFile(globalPath+".predict.txt");
		int nbLinePurelyPredicted=0;
		int nbConstrainedPredicted=0;
		String lineIn=inputFile.readLine();

		while (lineIn!=null) {
			String lineOut;
			if (isAcommentaryLine(lineIn)) {
				//lineOut=lineIn+" ==  identified as commentary";
				lineOut=lineIn;
			} else {

				//lineOut=lineIn+" == not identified "; // DEFAULT
				lineOut=lineIn;
				if (isAToPredicLine(lineIn)) { // CASE PURE "TO PREDICT"
					nbLinePurelyPredicted++;
					
					//================================================   Vendredi 20 novembre 2020  ======================
					//
					/*
					C'est ICI qu'il faut insérer une renormalisation de la chaine Line IN,
					Pour s'assurer qu'elle a un format      amorce tab => tab  
					ou bien un format amorce tab => tab liste de containtes tab
					A faire aussi pour CouldBeTrainingLine (à renommer en raining or Contrained !)
					*/
					lineIn=getToPredicLineNormalization(lineIn);
					
					String predict=getYStringPrediction(lineIn);
					//lineOut=lineIn+"\t//\t"+predict+" == identified as to predict ";
					lineOut=lineIn+myParams.predictionWriterPrefix+predict;
					if (myParams.addClusterInfoPrediction) {
						predict=getClusterPathPredictionForString(lineIn);
						lineOut=lineOut+myParams.predictionWriterPrefix+predict;
					}
				}
				if (couldBeATrainingOrConstrainedOrTestLine(lineIn)) {  // CASE CONSTRAINED PREDICTION VIA LIST OF LABELS
					
					//==================================================  Vendredi 20 novembre 2020 ========================
					//
					//Normalisation d'une ligne de type apprentissage / contrainte
					lineIn=getTrainingOrConstrainedPredictionlineInNormalization(lineIn);
					
					//String[] xy=lineIn.split(myParams.predictionWriterPrefix);
					String[] xy=lineIn.split(myParams.xySep);
					String predict;   //  ="// == identified as training or test or constrained prediction...";  //===========TODO
					String XPart=xy[0];
					String listOflabels=xy[1];
					predict=getYStringPredictionConstrained(XPart, listOflabels);
					//- if we can extract a list of label, without 
					lineOut=lineIn+myParams.predictionWriterPrefix+predict;
					nbConstrainedPredicted++;
					if (myParams.addClusterInfoPrediction) {
						predict=getClusterPathPredictionForString(XPart);
						lineOut=lineOut+myParams.predictionWriterPrefix+predict;
					}
				}
			}
			outputFile.writeRecord(lineOut);
			lineIn=inputFile.readLine();
		}
		inputFile.closeFile();
		outputFile.closeFile();

		String mess="number of records purely predicted:"+nbLinePurelyPredicted+"\t";
		mess=mess+"number of constrained records predicted: "+nbConstrainedPredicted;
		return mess ; 
	}



	/**
	 * save the model in RAM to disk 
	 * @param path
	 * @return message OK or error message
	 */
	public String file_saveModel(String path) {
		System.out.println("API TXT: saving model:"+path);
		//myModel = new CraftML();
		if (myModel==null) {
			System.out.println("no model in ram detected");
			return "error: no model in RAM to save";
		}
		System.out.println(getModelMainParameters());
		myModel.saveModel(path,modelForTextExtension);
		return "OK, model saved";
	}

	private String getModelMainParameters() {
		String result="";
		result=result+"nb Trees:"+myModel.nbTrees+"\n";
		result=result+"branch factor:"+myModel.branchFactor+"\n";
		result=result+"dim reduction X:"+myModel.dimReductionX+"\n";
		result=result+"dim reduction Y:"+myModel.dimReductionY+"\n";
		result=result+"sparsity:"+myModel.sparsity+"\n";
		result=result+"Reservoir sampling size:"+myModel.sizeReservoirKmeans+"\n";
		result=result+"min instance/leaf:"+myModel.minInst+"\n";
		result=result+"depth max:"+myModel.depthMax+"\n";
		return result;
	}

	/**
	 * load the model in RAM from to disk 
	 * @param globalPath
	 * @return message OK or error message
	 */
	public String file_loadModel(String path) {
		System.out.println("API TXT: loading:"+path);
		myModel = new CraftML();
		try {
			myModel.loadModel(path, modelForTextExtension);	
		} catch (Exception e) {
			System.out.println("error :"+e);
			myModel=null;
		}
		
		if (myModel.hasLearnt) {
			System.out.println(getModelMainParameters());
			return "OK, model loaded";
		} else {
			myModel=null;
			return "problem loading:"+path+modelExtensionName;
		}
	}


	//public static void main (String[] params) {
	//	System.out.println("tests initiaux internes...");
	//}


}

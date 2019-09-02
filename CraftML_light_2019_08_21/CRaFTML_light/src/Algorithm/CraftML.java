package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import FilesManagement.SmallItem;
import FilesManagement.RecordTextReader;
import FilesManagement.RecordTextWriter;
import FilesManagement.CraftMLFileReader;
import FilesManagement.CraftMLPredictionWriter;
import FilesManagement.LibsvmFileReader;
import FilesManagement.OnlinePerformanceCumulator;

/**
 * Main class of CRAFTML
 * 
 * @author XPGT4620
 *
 * 
 * Important: le booléen optimizeMemory définit si on utilise le mode craftml standard qui charge le dataset en mémoire (false) ou non (true).
 * 
 * Important: the optimizeMemory boolean defines whether to use the standard craftml mode that loads the dataset into memory (false) or not (true)
 * 
 * 
 */
public class CraftML {

	// Reading the dataset :
	
	public boolean modeCollaborativeFiltering = false;
	
	public boolean optimizeMemory = false;
	public boolean allTreesTogether = false;
	
	public boolean otherProjection = false;

	String inputFieldSeparator = " ";
	String inputLabelSeparator = ",";
	String inputKeyValueSeparator = ":";

	// Storing the dataset :

	ArrayList<float[]> valuesX;
	ArrayList<String[]> keyX;
	ArrayList<float[]> valuesY;
	ArrayList<String[]> keyY;

	int[] useInst;

	// Counters

	int nbInstancePredict;
	int nbInstance;

	// CRAFTML parameters

	public int nbTrees = 50;

	public int dimReductionX = 10000;
	public int dimReductionY = 10000;

	public int sparsity = 1000;

	public float nbThread = 1f;

	public String mode = "SXDY";

	public int branchFactor = 10;
	
	public int minInst = 10;

	public int sizeReservoirKmeans = 20000;

	public int depthMax = 800;

	// Indicator that the algorithm has been trained

	public boolean hasLearnt = false;

	// CRAFTML Trees

	public GenericNodeCraftML[] myTrees;

	// =========== Setters and Getters ==============

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setDimReductionX(int dimReductionX) {
		this.dimReductionX = dimReductionX;
	}

	public void setDimReductionY(int dimReductionY) {
		this.dimReductionY = dimReductionY;
	}

	public int getNbTrees() {
		return nbTrees;
	}

	public void setNbTrees(int nbTrees) {
		this.nbTrees = nbTrees;
	}

	public void setBranchFactor(int branchFactor) {
		this.branchFactor = branchFactor;
	}
	
	public void setOptimizeMemory(boolean optimizeMemory) {
		this.optimizeMemory = optimizeMemory;
	}

	public void setAllTreesTogether(boolean allTreesTogether) {
		this.allTreesTogether = allTreesTogether;
	}
	
	public void setMinInst(int minInst) {
		this.minInst = minInst;
	}
	
	public void messageTrace(String message) {
		Displayer.displayText(message);
	}
	
	public void progression(double ratio) {
		
	}
	
	public boolean interrompre() {
		return false;
	}

	/**
	 * (Re)initialize the forest and the dataset storage before training
	 */
	public void reinitialize() {

		if(optimizeMemory){
			myTrees = new NodeCraftML_OneLevelByPass[nbTrees];
			for (int i = 0; i < nbTrees; i++) {
				myTrees[i] = new NodeCraftML_OneLevelByPass(0, "tree" + Integer.toString(i), dimReductionX, dimReductionY, sparsity, mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
			}
		}else{
			myTrees = new NodeCraftML[nbTrees];
			for (int i = 0; i < nbTrees; i++) {
				myTrees[i] = new NodeCraftML(0, "tree" + Integer.toString(i), dimReductionX, dimReductionY, sparsity, mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
			}
			valuesX = new ArrayList<float[]>();
			valuesY = new ArrayList<float[]>();
			keyX = new ArrayList<String[]>();
			keyY = new ArrayList<String[]>();
		}
		

		nbInstance = 0;
		nbInstancePredict = 0;

		

		hasLearnt = false;

	}

	/**
	 * Adds the input training instance (x,y) to the dataset storage
	 * (valuesX,keyX,valuesY,keysY)
	 * 
	 * @param x
	 * @param y
	 */
	public void storeForLearning(SmallItem x, SmallItem y) {
		int i;
		if (x.getSize() == 0) {
			valuesX.add(null);
			keyX.add(null);
		} else {
			float[] currValX = new float[x.getSize()];
			String[] currKeyX = new String[x.getSize()];
			i = 0;
			for (String key : x.getKeySet()) {
				currKeyX[i] = key;
				currValX[i] = x.getValue(key);
				i++;
			}
			valuesX.add(currValX);
			keyX.add(currKeyX);
		}

		if (y.getSize() == 0) {
			valuesY.add(null);
			keyY.add(null);
		} else {
			float[] currValY = new float[y.getSize()];
			String[] currKeyY = new String[y.getSize()];
			i = 0;
			for (String key : y.getKeySet()) {
				currKeyY[i] = key;
				currValY[i] = y.getValue(key);
				i++;
			}
			valuesY.add(currValY);
			keyY.add(currKeyY);
		}
		nbInstance++;
	}
	
	
	/**
	 * learns function for the memory optimized mode (one tree)
	 * @param x
	 * @param y
	 * @param index
	 */
	public void learnOptMemTree(SmallItem x, SmallItem y,int index) {
		myTrees[index].learn(x, y);
	}
	
	/**
	 * learns function for the memory optimized mode (all trees)
	 * @param x
	 * @param y
	 */
	public void learnOptMemForest(SmallItem x, SmallItem y) {
		for(int i = 0; i < nbTrees;i++){
			myTrees[i].learn(x, y);
		}		
	}
	
	

	/**
	 * Shows the number of instances in each node of the tree
	 */
	public void showStats() {
		for (int i = 0; i < nbTrees; i++) {
			myTrees[i].showStats();
		}
	}

	/**
	 * Prepare the training thread for the tree of index "treeIndex"
	 * 
	 * @param treeIndex
	 * @return
	 */
	public Thread createTreeThread(int treeIndex) {
		Thread out = new Thread() {
			public void run() {

				long startTime = System.currentTimeMillis();

				messageTrace("Training tree " + treeIndex + " started");

				ArrayList<Integer> useInst = new ArrayList<Integer>();

				for (int j = 0; j < nbInstance; j++) {
					useInst.add(j);
				}

				myTrees[treeIndex].learn(keyX, valuesX, useInst, keyY, valuesY, 0);

				long estimatedTime = (System.currentTimeMillis() - startTime) / 1000;

				messageTrace("Training time tree " + treeIndex + ": " + estimatedTime + "s");

				messageTrace("Training tree " + treeIndex + " finished\n");		
				
			}
		};
		return out;
	}

	/**
	 * Returns the id of the leaf reached by x in the tree of index treeNumber.
	 * @param x
	 * @param treeNumber
	 * @return
	 */
	public GenericNodeCraftML getLeaf(SmallItem x,int treeNumber) {
		GenericNodeCraftML leaf;
		leaf = myTrees[treeNumber].getLeaf(x);
		return leaf;
	}
	
	/**
	 * Returns the ids of all the nodes reached by x in the tree of index treeNumber.
	 * @param x
	 * @param treeNumber
	 * @return
	 */
	public GenericNodeCraftML[] getPath(SmallItem x,int treeNumber) {
		ArrayList<GenericNodeCraftML> nodes = new ArrayList<GenericNodeCraftML>();
		myTrees[treeNumber].addPath(x, nodes);
		GenericNodeCraftML[] nodesArray = new GenericNodeCraftML[nodes.size()];
		nodes.toArray(nodesArray);
		return nodesArray;
	}
	
	
	public void setNodeNativeInformation(GenericNodeCraftML node, NativeInformation nativeInformation){
		node.addNativeInformation(nativeInformation);
	}
	
	
	public ArrayList<NativeInformation> getNodeNativeInformation(GenericNodeCraftML node){
		return node.getNativeInformation();
	}
	

	/**
	 * Prediction of the forest for the input features x
	 * 
	 * @param x
	 * @return
	 */
	public SmallItem predict(SmallItem x) {
		SmallItem ypred, ycurrent;
		ypred = new SmallItem();
		ypred.setID(x.getID());

		nbInstancePredict++;

		for (int i = 0; i < nbTrees; i++) {
			ycurrent = myTrees[i].predict(x);
			for (String key : ycurrent.getKeySet()) {
				if (ypred.hasKey(key)) {
					ypred.putKeyValue(key, ypred.getValue(key) + ycurrent.getValue(key));
				} else {
					ypred.putKeyValue(key, ycurrent.getValue(key));
				}
			}
		}

		for (String key : ypred.getKeySet()) {
			ypred.putKeyValue(key, ypred.getValue(key) / nbTrees);
		}

		return ypred;
	}

	/**
	 * Not implemented: make classification from prediction (thresholding)
	 * 
	 * @param x
	 * @return
	 */
	public SmallItem getClassificationOfLastPrediction(SmallItem x) {
		// TODO Auto-generated method stub
		return new SmallItem();
	}

	/**
	 * Trains all trees
	 */
	public void indicationFinDePasse() {


		for (int i = 0; i < (nbTrees / nbThread); i++) {

				//============ Progress bar for Philippe
				double denom = ((double)(nbTrees))/((double)(nbThread));			
				double ratio = ((double)(i))/denom;			
				progression(ratio);
				if(interrompre()) {
					return;
				}				
				//============

				int nbCurrThread = (int) (Math.min(nbThread * i + nbThread, nbTrees) - nbThread * i);

				Thread[] treesThreads = new Thread[nbCurrThread];

				for (int k = 0; k < treesThreads.length; k++) {
					treesThreads[k] = createTreeThread((int) (nbThread * i + k));
				}

				for (int k = 0; k < treesThreads.length; k++) {
					treesThreads[k].start();
				}

				for (int k = 0; k < treesThreads.length; k++) {
					try {
						treesThreads[k].join();
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				
		}
		
		progression(1);

	}
	
	
	public void indicationFinDePasseOptMem(int index) {
		myTrees[index].indicationFinDePasse();
	}
	
	public void indicationFinDePasseOptMemForest() {

		hasLearnt = true;
		for (int i = 0; i < nbTrees; i++) {
			myTrees[i].indicationFinDePasse();
			if(!myTrees[i].isTrained()){
				hasLearnt = false;
			}
		}
		
		
	}
	
	

	/**
	 * Displays an error and exits the program
	 * 
	 * @param mess
	 */
	public void error(String mess) {
		messageTrace("Error:\n" + mess);
		Error er = new Error(mess);
		throw er;
	}

	/**
	 * Loads a smallitem that corresponds to the feature vector of the instance
	 * at the input index in the stored dataset
	 * 
	 * @param index
	 * @return
	 */
	SmallItem loadX(int index) {
		if (index < keyX.size()) {
			if (keyX.get(index) == null) {
				return new SmallItem();
			} else {
				SmallItem out = new SmallItem();
				float[] currentVal = valuesX.get(index);
				String[] currentKey = keyX.get(index);
				for (int i = 0; i < currentVal.length; i++) {
					out.putKeyValue(currentKey[i], currentVal[i]);
				}
				return out;
			}
		} else {
			error("no existing example");
			return null;
		}
	}
	
	
	/**
	 * Loads a model from path and name
	 * @param loadPath
	 * @param modelName
	 */
	public void loadModel(String loadPath, String modelName){
		
		loadModelParameters(loadPath + modelName + "_parameters.txt");
		
		reinitialize();
		
		for(int i = 0;i<nbTrees;i++){
			
			long startTime = System.currentTimeMillis();
			
			messageTrace("Reading tree " + i);
			
			RecordTextReader mRecordTextReader = new RecordTextReader();
			
			mRecordTextReader.setSeparatorRecord("\t");		
			
			mRecordTextReader.openFile(loadPath + modelName + "_tree_" + Integer.toString(i) + ".txt");
	
			String[] record = mRecordTextReader.readPureRecord();
			
			myTrees[i].restoreNode(mRecordTextReader, record);
			
			mRecordTextReader.closeFile();
			
			// ... do something ... 
			long estimatedTime = (System.currentTimeMillis() - startTime)/1000;
			
			messageTrace("Reading time : " + estimatedTime + "s");	
			
		}	
		
		hasLearnt = true;
		
		auditForest(null);
		
	}
	
	public void loadModelParameters(String fileName){
		
		RecordTextReader mRecordTextReader = new RecordTextReader();
		mRecordTextReader.openFile(fileName);
		
		String line = mRecordTextReader.readLine();
				
		while (line != null) {
			String[] record = line.split("=");
			switch (record[0]) {
			case "nbTrees":
					nbTrees = Integer.parseInt(record[1]);
				break;
			case "dimX":
				dimReductionX = Integer.parseInt(record[1]);
			break;
			case "dimY":
				dimReductionY = Integer.parseInt(record[1]);
			break;
			case "sparsity":
				sparsity = Integer.parseInt(record[1]);
			break;
			case "mode":
				mode = record[1];
			break;
			case "branchFactor":
				branchFactor = Integer.parseInt(record[1]);
			break;
			case "sizeReservoirKmeans":
				sizeReservoirKmeans = Integer.parseInt(record[1]);
			break;
			case "depthMax":
				depthMax = Integer.parseInt(record[1]);
			break;
			default:
				break;
			}
			line = mRecordTextReader.readLine();	
		}
		
		mRecordTextReader.closeFile();
		
	}
	
	/**
	 * Saves the model at path with given name
	 * @param loadPath
	 * @param modelName
	 */
	public void saveModel(String savePath, String modelName){
		
		if(hasLearnt){
			
			saveModelParameters(savePath + modelName + "_parameters.txt");
			
			for(int i = 0;i<nbTrees;i++){
				long startTime = System.currentTimeMillis();
				
				RecordTextWriter mRecordTextWriter = new RecordTextWriter();
				
				mRecordTextWriter.setSeparator("\t");
				
				mRecordTextWriter.openFile(savePath + modelName + "_tree_" + Integer.toString(i) + ".txt");	
				
				myTrees[i].storeNode(mRecordTextWriter, null,null);
				
				mRecordTextWriter.closeFile();
				
				long estimatedTime = (System.currentTimeMillis() - startTime)/1000;
				
				messageTrace("Writing time tree " + i + ": " + estimatedTime + "s");
		
			}
			
		}else{
			error("You cannot save a non trained model");
		}	
		
	}
	
	public void saveModelParameters(String fileName){
		
		RecordTextWriter mRecordTextWriter = new RecordTextWriter();
		mRecordTextWriter.openFile(fileName);
		
		mRecordTextWriter.writeLine("nbTrees="+Integer.toString(nbTrees));
		mRecordTextWriter.writeLine("dimX="+Integer.toString(dimReductionX));
		mRecordTextWriter.writeLine("dimY="+Integer.toString(dimReductionY));
		mRecordTextWriter.writeLine("sparsity="+Integer.toString(sparsity));
		mRecordTextWriter.writeLine("mode="+mode);
		mRecordTextWriter.writeLine("branchFactor="+Integer.toString(branchFactor));
		mRecordTextWriter.writeLine("sizeReservoirKmeans="+Integer.toString(sizeReservoirKmeans));
		mRecordTextWriter.writeLine("depthMax="+Integer.toString(depthMax));		

		mRecordTextWriter.closeFile();
	}
	
	
	public void auditForest(String outFile){
		
		RecordTextWriter mWriter = null;
		
		if(outFile != null){
			mWriter = new RecordTextWriter();
			mWriter.setSeparator("\t");
			mWriter.openFile(outFile);			
		}
		
		String[] record = new String[10];
		record[0] = "Tree number";
		record[1] = "Nb nodes";
		record[2] = "Nb leaves";
		record[3] = "Tot inst/leaf";
		record[4] = "Max inst/leaf";
		record[5] = "Min inst/leaf";
		record[6] = "Mean inst/leaf";
		record[7] = "Mean leaf depth";
		record[8] = "Max leaf depth";
		record[9] = "Min leaf depth";
		
		messageTrace(Arrays.toString(record));
		
		if(outFile != null){
			mWriter.writeRecord(record);
		}
			
		
		for(int i = 0; i < nbTrees;i++){
			record[0] = Integer.toString(i);
			record[1] = Integer.toString(myTrees[i].getNbNode());
			record[2] = Integer.toString(myTrees[i].getNbLeaf());
			record[3] = Integer.toString(myTrees[i].getNbInstanceLeaves());
			record[4] = Integer.toString(myTrees[i].getMaxInstanceLeaf());
			record[5] = Integer.toString(myTrees[i].getMinInstanceLeaf());
			record[6] = Float.toString(((float) myTrees[i].getNbInstanceLeaves())/((float) myTrees[i].getNbLeaf()));
			record[7] = Float.toString(((float) myTrees[i].sumDepthLeaf())/((float) myTrees[i].getNbLeaf()));
			record[8] = Integer.toString(myTrees[i].getMaxDepthLeaf());
			record[9] = Integer.toString(myTrees[i].getMinDepthLeaf());
			
			if(outFile != null){
				mWriter.writeRecord(record);
			}			
			
			messageTrace(Arrays.toString(record));
			
		}	
		
		if(outFile != null){
			mWriter.closeFile();			
		}
		
	}
	

	/**
	 * Trains the forest from the given FileReader
	 * 
	 * @param filePath
	 */
	public void trainAlgoOnFile(CraftMLFileReader fileReader) {
		if(!fileReader.isReady()){
			error("You have to set the file path in fileReader before calling trainAlgoOnFile");
		}else{
			if(optimizeMemory){
				if(allTreesTogether){
					trainAlgoOnFileOptMemForest(fileReader);
				}else{
					trainAlgoOnFileOptMemTreeByTree(fileReader);
				}
			}else{				
				trainAlgoOnFileStandard(fileReader);
			}
		}	
	}
	
	
	public void trainAlgoOnFileStandard(CraftMLFileReader fileReader) {
		fileReader.openFile();
		
		reinitialize();
		SmallItem x;
		SmallItem y;
		boolean continueRead = fileReader.readNext();
		while(continueRead){
			if(interrompre()) {
				return;
			}
			x = fileReader.getX();
			y = fileReader.getY();
			check(x, y);
			storeForLearning(x, y);
			continueRead = fileReader.readNext();
		}
		indicationFinDePasse();
		hasLearnt = true;
		fileReader.closeFile();
	}
		
	
	public void trainAlgoOnFileOptMemTreeByTree(CraftMLFileReader fileReader) {


		reinitialize();

		SmallItem x;
		SmallItem y;
		
		
		for(int i = 0; i < nbTrees; i++){
			messageTrace("Training tree " + i);
			
			while(myTrees[i].isTrained() == false){
				
				fileReader.openFile();
				
				nbInstance = 0;

				boolean continueRead = fileReader.readNext();

				while (continueRead) {
					if(interrompre()) {
						return;
					}
					x = fileReader.getX();
					y = fileReader.getY();
					check(x, y);
					nbInstance++;
					learnOptMemTree(x, y,i);
					continueRead = fileReader.readNext();
				}

				indicationFinDePasseOptMem(i);
				fileReader.closeFile();			
			}
		}
		
		hasLearnt = true;
	}
	
	public void trainAlgoOnFileOptMemForest(CraftMLFileReader fileReader) {
				
		reinitialize();

		SmallItem x;
		SmallItem y;
		
		while(hasLearnt == false){
			
			fileReader.openFile();
			nbInstance = 0;
			boolean continueRead = fileReader.readNext();

			while (continueRead) {
				if(interrompre()) {
					return;
				}
				x = fileReader.getX();
				y = fileReader.getY();
				check(x, y);
				nbInstance++;
				learnOptMemForest(x, y);
				continueRead = fileReader.readNext();
			}
			indicationFinDePasseOptMemForest();
			fileReader.closeFile();						
		}				
	}
	

	

	/**
	 * Make predictions and evaluates performances for the given test file (optional : write predictions)
	 * 
	 */
	public void mesurePerformanceOnFile(CraftMLFileReader fileReader,List<String> performances, boolean writePerformances, String filePerformances, boolean writePrediction,CraftMLPredictionWriter predictionWriter,int topN) {
		if(!interrompre()) {
			if(!fileReader.isReady()){
				error("You have to set the file path in fileReader before calling predictOnFile");
			}else{
				int numberOfRows = fileReader.countLines();
				fileReader.openFile();
				if (hasLearnt) {
					
					SmallItem x;
					SmallItem y;

					SmallItem ypred, yclass;				

					OnlinePerformanceCumulator performanceCumulator = new OnlinePerformanceCumulator(performances);

					// a enlever ?
					int indexCurrent = 0;

					messageTrace("\n Starting predictions and evaluation on test file... \n");

					boolean continueRead = fileReader.readNext();

					while (continueRead) {
						
						double ratio =  ((double)(indexCurrent))/((double)(numberOfRows));
						progression(ratio);
						if(interrompre()) {
							return;
						}
						
						indexCurrent++;
						
						x = fileReader.getX();					
						y = fileReader.getY();		
						
						check(x, y);

						ypred = predict(x);
						
						if(modeCollaborativeFiltering){
							ypred.deleteReferenceKeys(x);
						}
						
						yclass = getClassificationOfLastPrediction(x);
						
						performanceCumulator.addError(y, ypred, yclass);
						
						if(writePrediction){
							predictionWriter.writePrediction(x, ypred.topN(topN));
						}
						
						continueRead = fileReader.readNext();
					}

					
					if(writePerformances){
						RecordTextWriter mWriter = new RecordTextWriter();
						mWriter.openFile(filePerformances);	
						for (int i = 0; i < performances.size(); i++) {
							mWriter.writeLine(performances.get(i) + "\t" + performanceCumulator.getPerformance(performances.get(i)));
						}
						mWriter.closeFile();
					}
					
					messageTrace("Testing performances computed on " + indexCurrent + " instances.");
					for (int i = 0; i < performances.size(); i++) {
						messageTrace(performances.get(i) + "\t" + performanceCumulator.getPerformance(performances.get(i)));
					}
					
					if(writePrediction){				
						predictionWriter.close();
					}
					

					messageTrace("\n Done. \n");

				} else {
					error("Algo not trained");
				}
			}		
		}
	}
	
	
	
	public void check(SmallItem x,SmallItem y){
		
	}
	
	
	/**
	 * Make predictions and evaluates performances for the given test file (optional : write predictions)
	 * 
	 */
	public void predictOnFile(CraftMLFileReader fileReader, CraftMLPredictionWriter predictionWriter,int topN) {
		if(!interrompre()) {
			if(!fileReader.isReady()){
				error("You have to set the file path in fileReader before calling predictOnFile");
			}else{
				int numberOfRows = fileReader.countLines();
				fileReader.openFile();
				if (hasLearnt) {
					
					SmallItem x;
					SmallItem y;

					SmallItem ypred, yclass;				

					// a enlever ?
					int indexCurrent = 0;

					messageTrace("\n Starting predictions on file... \n");

					boolean continueRead = fileReader.readNext();

					while (continueRead) {
						double ratio =  ((double)(indexCurrent))/((double)(numberOfRows));
						progression(ratio);
						if(interrompre()) {
							return;
						}
						indexCurrent++;
						
						x = fileReader.getX();					
						y = fileReader.getY();			
						check(x, y);

						
						
						ypred = predict(x);					
						
						if(modeCollaborativeFiltering){
							ypred.deleteReferenceKeys(x);
						}
						
						predictionWriter.writePrediction(x, ypred.topN(topN));
						continueRead = fileReader.readNext();
					}

					predictionWriter.close();
					

					messageTrace("\n Done. \n");

				} else {
					error("Algo not trained");
				}
			}
		}
	}	
	

	public static void main(String[] args) {

		List<String> performances = new ArrayList<String>();
		performances.add("Pat1");
		performances.add("Pat3");
		performances.add("Pat5");
		/*
		 * String trainFile =
		 * "C:/benchWissam2016_2017/bibtex_xml/bibtex_train_fold1.txt"; String
		 * testFile =
		 * "C:/benchWissam2016_2017/bibtex_xml/bibtex_test_fold1.txt";
		 * 
		 * String xVarFile =
		 * "C:/benchWissam2016_2017/bibtex_xml/bibtex_x_variables.txt"; String
		 * yVarFile =
		 * "C:/benchWissam2016_2017/bibtex_xml/bibtex_y_variables.txt";
		 */

		/*
		 * String trainFile = "C:/benchWissam2016_2017/Wiki10/Wiki10_train.txt";
		 * String testFile = "C:/benchWissam2016_2017/Wiki10/Wiki10_test.txt";
		 * 
		 * String xVarFile =
		 * "C:/benchWissam2016_2017/Wiki10/Wiki10_x_variables.txt"; String
		 * yVarFile = "C:/benchWissam2016_2017/Wiki10/Wiki10_y_variables.txt";
		 */


		CraftML model = new CraftML();
		
		if (args.length == 2) {
			TreeMap<String, String> params = new TreeMap<String, String>();
			boolean pb_format = false;
			for (int i = 0; i < 2; i++) {
				if (args[i].indexOf("=") < 0) {
					pb_format = true;
				} else {
					String[] currSpl = args[i].split("=");
					if (currSpl.length != 2) {
						pb_format = true;
					} else {
						params.put(currSpl[0], currSpl[1]);
					}
				}
			}
			if (!pb_format) {
				boolean pb_params = !(params.containsKey("train_file") && params.containsKey("test_file"));
				if (!pb_params) {
					
					model.messageTrace("Starting CRAFTML with the following parameters : ");
					model.messageTrace("\t Input train file : " + params.get("train_file"));
					model.messageTrace("\t Input test file : " + params.get("test_file"));

					String trainFile = params.get("train_file");
					String testFile = params.get("test_file");

					/*
					 * 
					 * 
					 * String trainFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_train.txt"; String
					 * testFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_test.txt";
					 * 
					 * String xVarFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_x_variables.txt";
					 * String yVarFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_y_variables.txt";
					 */

					/*
					 * String trainFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_train.txt";
					 * String testFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_test.txt";
					 * 
					 * String xVarFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_x_variables.txt";
					 * String yVarFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_y_variables.txt";
					 */

					

					model.setMode("DXDY");
					
					
					LibsvmFileReader readerTrain = new LibsvmFileReader();
					
					readerTrain.setFile(trainFile);
					
					model.trainAlgoOnFile(readerTrain);
					
					LibsvmFileReader readerTest = new LibsvmFileReader();
					
					readerTest.setFile(testFile);
					
					model.mesurePerformanceOnFile(readerTest,performances,false,null, false,null,0);
					
					//model.loadModel("C:/modelCraftSaved/", "yolo");
					
					//model.predictOnFile(testFile,true,"C:/modelCraftSaved/predTest.txt");
					

					//model.saveModel("C:/modelCraftSaved/", "yolo");
					

				} else {
					model.messageTrace("Error: The two parameters don't match with: train_file,test_file");
				}
			} else {
				model.messageTrace(
						"Error: the parameters format must be paramname=paramvalue. Note: This error occurs if the file path contains the character '='.");
			}

		} else if (args.length == 3) {
			TreeMap<String, String> params = new TreeMap<String, String>();
			boolean pb_format = false;
			for (int i = 0; i < 3; i++) {
				if (args[i].indexOf("=") < 0) {
					pb_format = true;
				} else {
					String[] currSpl = args[i].split("=");
					if (currSpl.length != 2) {
						pb_format = true;
					} else {
						params.put(currSpl[0], currSpl[1]);
					}
				}
			}
			if (!pb_format) {
				boolean pb_params = !(params.containsKey("train_file") && params.containsKey("test_file")
						&& params.containsKey("nb_tree"));
				if (!pb_params) {

					model.messageTrace("Starting CRAFTML with the following parameters : ");
					model.messageTrace("\t Input train file : " + params.get("train_file"));
					model.messageTrace("\t Input test file : " + params.get("test_file"));
					model.messageTrace("\t Number of trees : " + params.get("nb_tree"));

					String trainFile = params.get("train_file");
					String testFile = params.get("test_file");

					int nbtree = Integer.parseInt(params.get("nb_tree"));

					/*
					 * 
					 * 
					 * String trainFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_train.txt"; String
					 * testFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_test.txt";
					 * 
					 * String xVarFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_x_variables.txt";
					 * String yVarFile =
					 * "C:/benchWissam2016_2017/Eurlex/Eurlex_y_variables.txt";
					 */

					/*
					 * String trainFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_train.txt";
					 * String testFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_test.txt";
					 * 
					 * String xVarFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_x_variables.txt";
					 * String yVarFile =
					 * "C:/benchWissam2016_2017/mediamill_xml/mediamill_xml_y_variables.txt";
					 */

					model.setNbTrees(nbtree);

					model.setMode("SXSY");

					LibsvmFileReader readerTrain = new LibsvmFileReader();
					
					readerTrain.setFile(trainFile);
					
					model.trainAlgoOnFile(readerTrain);
					
					LibsvmFileReader readerTest = new LibsvmFileReader();
					
					readerTest.setFile(testFile);
					
					model.mesurePerformanceOnFile(readerTest,performances,false,null, false,null,0);
					
					model.auditForest("C:/benchWissam2016_2017/testAudit.txt");
					
					

				} else {
					model.messageTrace("Error: The three parameters don't match with: train_file,test_file, nb_tree");
				}
			} else {
				model.messageTrace(
						"Error: the parameters format must be paramname=paramvalue. Note: This error also occurs if the file path contains the character '='.");
			}
		} else {
			model.messageTrace(
					"Error: There must be at most 3 parameters and at least the 2 following parameters: train_file,test_file");
		}

	}
}

package textModule;

import java.util.Scanner;
import utils.MessManager;

public class CraftML4Text_Command {

	
	static public final String hello="Hello, type HELP for help, or a command, or 'bye' to quit";
	
	static final String modelName4txt="CraftTXT";
	
	CraftML4Text_API myAPi=new CraftML4Text_API();
	
	
	/**
	 * Checks if the file exists and is openable
	 * @param globalPath
	 * @return
	 */
	static public boolean isOpenable(String globalPath) {
		boolean result=false;
		SimpleTextReaderUTF8 myFile= new SimpleTextReaderUTF8();
		result=myFile.openFile(globalPath);
		if (result) {
			myFile.closeFile();
			return true;
		} else {
			System.out.println("problem opening: "+globalPath);   //---------------  TODO : use MessManager
		}
		return result;
	}
	
	/**
	 * 
	 * @param line   line that is not necessary in lowerCase
	 * @param that    that is in lowerCase
	 * @return  result not necessary in lowerCase 
	 */
	static public String getParamAfterThat(String line, String that) {
		assert (line!=null);
		assert (that!=null);
		String lineBefore=line;
		line=line.toLowerCase();
		int pos=line.indexOf(that);
		assert (pos>=0);
		String result;
		int start=pos+that.length();
		result=lineBefore.substring(start);
		result=result.trim();
		return result;
	}
	
	/**
	 * Main Interpretor of commands for CraftML For Text
	 * @param commandLine
	 * @return
	 */
	public String commandInterpretor(String commandLine) {
		String cmd=commandLine.trim().toLowerCase();
		String result="error: command not recognized: "+commandLine;
		//boolean commandRecognized=false;
		
		if (cmd.startsWith("load ")) {
			String loadPath=getParamAfterThat(commandLine, "load ");
			
			//myAPi.myModel.loadModel(loadPath, modelName4txt);
			result=myAPi.file_loadModel(loadPath);
			//result="Load model OK";
			return result;
			
		}
		
		if (cmd.startsWith("save ")) {
			String savePath=getParamAfterThat(commandLine, "save ");
			//if (isOpenable(savePath)) {                                       
				//myAPi.myModel.loadModel(l
			//myAPi.myModel.loadModel(loadPath, modelName4txt);
			result=myAPi.file_saveModel(savePath);
			//result="save model OK";
			return result;
			//}
		}
		
		if (cmd.startsWith("learn ")) {
			String learnGlobalPath=getParamAfterThat(commandLine, "learn");
			
			//myAPi.myModel.loadModel(loadPath, modelName4txt);
			result=myAPi.file_learnModelFromFile(learnGlobalPath);
			return result;
			
		}
		
		
		if (cmd.startsWith("predict ")) {
			String predicGlobalPath=getParamAfterThat(commandLine, "predict ");
			if (isOpenable(predicGlobalPath)) {
				result=myAPi.file_predictOnInteractiveFile(predicGlobalPath);
			return result;
			} else {
				return "error: predict: unable to open prediction file: "+predicGlobalPath;
			}
		}
		
		
		if (cmd.startsWith("eval ")) {
			String predicGlobalPath=getParamAfterThat(commandLine, "eval ");
			if (isOpenable(predicGlobalPath)) {
				result=myAPi.file_eval_precision(predicGlobalPath);
			return result;
			} else {
				return "error: eval: unable to open test file: "+predicGlobalPath;
			}
		}
		
		
		if (cmd.startsWith("setnumberoftrees ")) {
			String numberOfTreeString=getParamAfterThat(commandLine,"setnumberoftrees ");
			
			try {
				int k=Integer.parseInt(numberOfTreeString);
				myAPi.myParams.numberOfTrees=k;
				return "number of trees set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setNumberOfTrees, integer expected, found: "+numberOfTreeString;
			}
		}
		
		
		if (cmd.startsWith("setdimx ")) {
			String setDimXString=getParamAfterThat(commandLine,"setdimx ");
			
			try {
				int k=Integer.parseInt(setDimXString);
				myAPi.myParams.xProjectionSize=k;
				return "dimension of projection for space X (text) set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setDimX, integer expected, found: "+setDimXString;
			}
		}
		
		
		if (cmd.startsWith("setdimy ")) {
			String setDimYString=getParamAfterThat(commandLine,"setdimy ");
			
			try {
				int k=Integer.parseInt(setDimYString);
				myAPi.myParams.yProjectionSize=k;
				return "dimension of projection for space Y (text) set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setDimY, integer expected, found: "+setDimYString;
			}
		}
		
		
		
		
		
		if (cmd.startsWith("setbranchfactor ")) {
			String branchFactorString=getParamAfterThat(commandLine,"setbranchfactor ");
			
			try {
				int k=Integer.parseInt(branchFactorString);
				myAPi.myParams.branchFactor=k;
				return "branch factor set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setBranchFactor, integer expected, found: "+branchFactorString;
			}
		}
		
		
		if (cmd.startsWith("setsizereservoir ")) {
			String branchFactorString=getParamAfterThat(commandLine,"setsizereservoir ");
			
			try {
				int k=Integer.parseInt(branchFactorString);
				myAPi.myParams.sizeReservoirSampling4Kmeans=k;
				return "size of node reservoir set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setSizeReservoir, integer expected, found: "+branchFactorString;
			}
		}
		
		
		if (cmd.startsWith("setnumberofthreads ")) {
			String branchFactorString=getParamAfterThat(commandLine,"setnumberofthreads ");
			try {
				int k=Integer.parseInt(branchFactorString);
				myAPi.myParams.nbThread=k;
				return "number of Threads set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setNumberOfThreads, integer expected, found: "+branchFactorString;
			}
		}
		
		if (cmd.startsWith("settopnmax ")) {
			String branchFactorString=getParamAfterThat(commandLine,"settopnmax ");
			try {
				int k=Integer.parseInt(branchFactorString);
				myAPi.myParams.topNLabels=k;
				return "number max of top N labels predicted per leaf set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setTopNMax, integer expected, found: "+branchFactorString;
			}
		}
		
		if (cmd.startsWith("setmininstancesinleaf ")) {
			String branchFactorString=getParamAfterThat(commandLine,"setmininstancesinleaf ");
			try {
				int k=Integer.parseInt(branchFactorString);
				myAPi.myParams.minInstanceInLeaf=k;
				return "number min of instance in leaf set to "+k;
			} catch (Exception e) {
				// TODO: handle exception
				return "error: setMinInstancesInLeaf, integer expected, found: "+branchFactorString;
			}
		}
		
		if (cmd.startsWith("setsparsity ")) {
			String branchFactorString=getParamAfterThat(commandLine,"setsparsity ");
			try {
				int k=Integer.parseInt(branchFactorString);
				if (k>myAPi.myParams.xProjectionSize) {
					return "error: sparsity must be lower than dimX (projection size for X space), currently dimX="+myAPi.myParams.xProjectionSize+" and sparsity cannot be set to: "+k;
				}
				myAPi.myParams.sparsity=k;
				return "sparsity (number of feature actually kept for each node separator) set to "+k;
			} catch (Exception e) {
				//  handle exception
				return "error: setSparsity, integer expected, found: "+branchFactorString;
			}
		}
		
		
		if (cmd.startsWith("setwordngrams ")) {
			String setwordngramsString=getParamAfterThat(commandLine,"setwordngrams ");
			try {
				int k=Integer.parseInt(setwordngramsString);
				if (k>10) {
					return "error: max size word ngrams is fixed to 10 and cannot be set to: "+k;
				}
				if (k<1) {
					return "error: min size word ngrams is fixed to 1 and cannot be set to: "+k;
				}
				myAPi.myParams.maxWordNgram=k;
				return "ngram word size set to "+k;
			} catch (Exception e) {
				//  handle exception
				return "error: setWordNgrams, integer expected, found: "+setwordngramsString;
			}
		}
		
		
		if (cmd.startsWith("setcharngrams ")) {
			String setCharngramsString=getParamAfterThat(commandLine,"setcharngrams ");
			try {
				int k=Integer.parseInt(setCharngramsString);
				if (k>10) {
					return "error: max size letter ngrams is fixed to 10 and cannot be set to: "+k;
				}
				if (k<0) {
					return "error: min size word ngrams is fixed to 0 (no letter ngram) and cannot be set to: "+k;
				}
				myAPi.myParams.maxCharNgram=k;
				if (k==0) {
					return "ngram char size set to 0 (no char ngram will be generated)";
				}
				return "ngram char size set to "+k;
			} catch (Exception e) {
				//  handle exception
				return "error: setWordNgrams, integer expected, found: "+setCharngramsString;
			}
		}
		
		
		//removePunctuation : TODO (default is no)
		if (cmd.startsWith("remove punctuation ")) {
			String removePunct=getParamAfterThat(commandLine,"remove punctuation  ");
			
			removePunct=removePunct.toLowerCase();
			if (removePunct.equals("true")) {
				myAPi.myParams.removePunctuation=true;
				return "remove punctuation during parsing: true";
			} 
			if (removePunct.equals("false")) {
				myAPi.myParams.removePunctuation=false;
				return "remove punctuation during parsing: false";
			}
			return "error: remove punctuation: expecting TRUE or FALSE, finding: "+removePunct;
			
		}
		
		
		
		return result;
	}
	
	
	
	
	public static void main (String[] p) {
		
		CraftML4Text_Command myConsole= new CraftML4Text_Command();
		
		//String cmd="load model c:/myModels/model1";
		//String param=getParamAfterThat(cmd, "load model");
		//System.out.println("["+param+"]");
		
		Scanner keyboard = new Scanner(System.in);
		
		boolean todo=true;
		System.out.println(CraftML4Text_Params.infoVersion);
		System.out.println(hello);
		while (todo) {
			System.out.print("> ");
			String commandLine=keyboard.nextLine();
			commandLine=commandLine.trim();
			String gen=commandLine.toLowerCase();
			if (gen.equals("bye")||gen.equals("quit")) {
				todo=false;
			} else {
				String result=myConsole.commandInterpretor(commandLine);
				MessManager.say(result);
			}
			
		}
		System.out.println("bye...");
		
		
		
		
		
		
	}
	
}

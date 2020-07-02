package Algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import FilesManagement.CraftMLFileReader;
import FilesManagement.CraftMLPredictionWriter;
import FilesManagement.LibsvmFileReader;
import FilesManagement.RecordTextReader;
import FilesManagement.TabularFileReader;

public class CraftML_API {
		
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
	
	
	public void messageTrace(String message) {
		Displayer.displayText(message);
	}
	
	public void checkConfigFile(String filename){
		RecordTextReader mReader = new RecordTextReader();
		mReader.openFile(filename);
				
		//checkMode;
		int nbTimesAction = 0;
		String line = mReader.readLine();
		String[] record;
		while(line != null){
			if(!line.startsWith("#")){
				record = line.split("=");
				if(record.length!=2){
					error("Problem with parsing at line : " + line);
				}else{
					if(record[0].equals("action")){
						nbTimesAction++;
					}
				}
			}
			line = mReader.readLine();
		}
		
		mReader.closeFile();
		
		if(nbTimesAction == 1){
			mReader = new RecordTextReader();
			mReader.openFile(filename);
			
			ArrayList<String> parameters = new ArrayList<>();
			ArrayList<String> values = new ArrayList<>();
			String action = null;
			
			line = mReader.readLine();
			while(line != null){
				if(!line.startsWith("#")){					
					record = line.split("=");
					if(record[0].equals("action")){
						action = record[1];
					}else{
						parameters.add(record[0]);
						values.add(record[1]);
					}					
				}
				line = mReader.readLine();
			}
			
			checkParametersConsistency(parameters,values,action);
			
		}else if (nbTimesAction == 0){
			error("Parameter 'action' is missing in config file !");
		}else{
			error("Parameter 'action' appears more than once in config file !");
		}
		
		
		
	}
	
	
	private void checkMeasures(String[] measures){
		
		//TODO ajouter plus tard les autres mesures autorisees
		
	}
	

	private void checkParametersConsistency(ArrayList<String> parameters, ArrayList<String> values,String action) {
	
		ArrayList<String> strictlyContains = new ArrayList<>();
		ArrayList<String> contains = new ArrayList<>();		
		
		contains.add("nbTree");
		contains.add("topN");
		contains.add("depthMax");
		contains.add("branchFactor");
		contains.add("dimProjX");
		contains.add("dimProjY");
		contains.add("sparsity");
		contains.add("sizeReservoirKmeans");
		contains.add("minInstLeaf");
		contains.add("optimizeMemory");
		contains.add("allTreesTogether");
		
		if(parameters.indexOf("inputFilesType") == -1) {
			error("parameter inputFilesType is missing");
		}else {
			int indexType = parameters.indexOf("inputFilesType");
			if(values.get(indexType).equals("tabular")) {
				strictlyContains.add("tabularClassPrefix");
				strictlyContains.add("tabularFieldDelim");
			}
		}
		
		checkAction(action);
		
		switch (action) {
		case "learnAndWrite":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("inputTrainFile");
			strictlyContains.add("outputPath");
			strictlyContains.add("modelName");
			break;
		case "learnAndPredict":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("inputTrainFile");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("outputPredictFile");
			break;
		case "learnPredictAndMeasure":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("inputTrainFile");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("outputPredictFile");
			strictlyContains.add("listOfMetrics");
			strictlyContains.add("outputMeasureFile");
			break;
		case "learnAndMeasure":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("inputTrainFile");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("listOfMetrics");
			strictlyContains.add("outputMeasureFile");
			break;
		case "readModelAndPredict":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("modelPath");
			strictlyContains.add("modelName");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("outputPredictFile");
			break;
		case "readModelPredictAndMeasure":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("modelPath");
			strictlyContains.add("modelName");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("outputPredictFile");
			strictlyContains.add("listOfMetrics");
			strictlyContains.add("outputMeasureFile");
			break;
		case "readModelAndMeasure":
			strictlyContains.add("inputFilesType");
			strictlyContains.add("modelPath");
			strictlyContains.add("modelName");
			strictlyContains.add("inputPredictFile");
			strictlyContains.add("listOfMetrics");
			strictlyContains.add("outputMeasureFile");
			break;
		default:
			error("Invalid action : " + action);
			break;
		}
		
		checkSetParameters(parameters,values,contains,strictlyContains,action);
		
	}

	
	
	private void checkSetParameters(ArrayList<String> parameters, ArrayList<String> values, ArrayList<String> contains, ArrayList<String> strictlyContains,String action) {
		
		int[] containsOccurrences = new int[contains.size()];
		int[] strictlyContainsOccurrences = new int[strictlyContains.size()];
		
		int indexContains;
		int indexStrictlyContains;
		
		for(int i = 0; i < parameters.size();i++){
			
			indexContains = contains.indexOf(parameters.get(i));
			indexStrictlyContains = strictlyContains.indexOf(parameters.get(i));
			
			if(indexContains == -1 && indexStrictlyContains == -1){
				error("Extra parameter : " + parameters.get(i));
			}else if (indexContains == -1 && indexStrictlyContains != -1){
				strictlyContainsOccurrences[indexStrictlyContains] = strictlyContainsOccurrences[indexStrictlyContains] + 1;
			}else if (indexContains != -1 && indexStrictlyContains == -1){
				containsOccurrences[indexContains] = containsOccurrences[indexContains] + 1;
			}else{
				error("Parameter cannot be in both contains and strictly contains: check API code");
			}
		}
		
		String errorSummary = "";
		boolean err = false;
		for(int i = 0;i<containsOccurrences.length;i++){
			if(containsOccurrences[i] > 1){
				errorSummary = errorSummary + "\n too many occurrences of parameter : " + contains.get(i);
				err = true;
			}
		}
		for(int i = 0;i<strictlyContainsOccurrences.length;i++){
			if(strictlyContainsOccurrences[i] > 1){
				errorSummary = errorSummary + "\n too many occurrences of parameter : " + strictlyContains.get(i);
				err = true;
			}else if (strictlyContainsOccurrences[i] == 0){
				errorSummary = errorSummary + "\n missing parameter : " + strictlyContains.get(i);
				err = true;
			}
		}
		
		
		if(err){
			error("Please correct the following errors for action " + action + ":" + errorSummary);
		}
		
		
	}

	private void checkAction(String action) {
		if(!action.equals("learnAndWrite") && !action.equals("learnAndPredict") && !action.equals("learnPredictAndMeasure") && !action.equals("learnAndMeasure") && !action.equals("readModelAndPredict") && !action.equals("readModelPredictAndMeasure") && !action.equals("readModelAndMeasure")){
			error("Invalid action : " + action);
		}
	}

	public static void main(String[] args) {
		CraftML_API mApi = new CraftML_API();
		if (args.length == 1) {
			//Checking if file exists
			File f = new File(args[0]);
			if(f.exists() && !f.isDirectory()) { 
			    mApi.checkConfigFile(args[0]);
			    
			    // loading parameters
			    
			    RecordTextReader mReader = new RecordTextReader();
				
				TreeMap<String,String> parameters = new TreeMap<>();
				String action = null;
				
				mReader.openFile(args[0]);
				
				String line = mReader.readLine();
				while(line != null){
					if(!line.startsWith("#")){					
						String[] record = line.split("=");
						if(record[0].equals("action")){
							action = record[1];
						}else{
							parameters.put(record[0],record[1]);
						}					
					}
					line = mReader.readLine();
				}
			    mReader.closeFile();
				
			    mApi.runCraftml(parameters,action);						
				
			    
			}else{
				mApi.error("File '" + args[0] + "' not found !");
			}
		}else{
			mApi.error("There must be exactly 1 parameter : full path to config file. Example : CRAFTML C:/Documents/config.txt");
		}
		
	}
	
	
	public void execute(String filePath){
		//Checking if file exists
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()) { 
		    checkConfigFile(filePath);
		    
		    // loading parameters
		    
		    RecordTextReader mReader = new RecordTextReader();
			
			TreeMap<String,String> parameters = new TreeMap<>();
			String action = null;
			
			mReader.openFile(filePath);
			
			String line = mReader.readLine();
			while(line != null){
				if(!line.startsWith("#")){					
					String[] record = line.split("=");
					if(record[0].equals("action")){
						action = record[1];
					}else{
						parameters.put(record[0],record[1]);
					}					
				}
				line = mReader.readLine();
			}
		    mReader.closeFile();
			runCraftml(parameters,action);		
		    
		}else{
			error("File '" + filePath + "' not found !");
		}
	}

	public void runCraftml(TreeMap<String, String> parameters, String action) {
		//instanciate craftml
		CraftML model = new CraftML();
		String rawMetrics = null;
		List<String> performances = null;
		//model parameters

		messageTrace("Action = " + action);
		messageTrace("");
		
		int topN = 20;
		
		if(parameters.containsKey("nbTree")){
			try {
				int nbtree = Integer.parseInt(parameters.get("nbTree"));				   
				model.setNbTrees(nbtree);
				messageTrace("Number of trees : " + nbtree);
			} catch (NumberFormatException e) {
				error("nbTree is not an integer");
			}
		}
		
		if(parameters.containsKey("topN")){
			try {
				topN = Integer.parseInt(parameters.get("topN"));	
				messageTrace("TopN kept for label predictions : " + topN);
			} catch (NumberFormatException e) {
				error("topN is not an integer");
			}
		}
		
		if(parameters.containsKey("depthMax")){
			try {
				int depthMax = Integer.parseInt(parameters.get("depthMax"));				   
				model.depthMax = depthMax;
				messageTrace("Depth max : " + depthMax);
			} catch (NumberFormatException e) {
				error("depthMax is not an integer");
			}
		}
		
		if(parameters.containsKey("branchFactor")){
			try {
				int branchFactor = Integer.parseInt(parameters.get("branchFactor"));
				model.setBranchFactor(branchFactor);
				messageTrace("Branch factor = " + branchFactor);
			} catch (NumberFormatException e) {
				error("branchFactor is not an integer");
			}
		}
		
		if(parameters.containsKey("dimProjX")){
			try {
				int dimProjX = Integer.parseInt(parameters.get("dimProjX"));				   
				model.setDimReductionX(dimProjX);
				messageTrace("Dim X = " + dimProjX);
			} catch (NumberFormatException e) {
				error("dimProjX is not an integer");
			}
		}
		
		if(parameters.containsKey("dimProjY")){
			try {
				int dimProjY = Integer.parseInt(parameters.get("dimProjY"));				   
				model.setDimReductionY(dimProjY);
				messageTrace("Dim Y = " + dimProjY);
			} catch (NumberFormatException e) {
				error("dimProjY is not an integer");
			}
		}
		
		if(parameters.containsKey("sparsity")){
			try {
				int sparsity = Integer.parseInt(parameters.get("sparsity"));				   
				model.sparsity = sparsity;
				messageTrace("Sparsity = " + sparsity);
			} catch (NumberFormatException e) {
				error("sparsity is not an integer");
			}
		}
		
		if(parameters.containsKey("sizeReservoirKmeans")){
			try {
				int sizeReservoirKmeans = Integer.parseInt(parameters.get("sizeReservoirKmeans"));				   
				model.sizeReservoirKmeans = sizeReservoirKmeans;
				messageTrace("Size reservoir kmeans = " + sizeReservoirKmeans);
			} catch (NumberFormatException e) {
				error("sizeReservoirKmeans is not an integer");
			}
		}
		
		if(parameters.containsKey("minInstLeaf")){
			try {
				int minInstLeaf = Integer.parseInt(parameters.get("minInstLeaf"));				   
				model.setMinInst(minInstLeaf);
				messageTrace("Min inst per leaf = " + minInstLeaf);
			} catch (NumberFormatException e) {
				error("minInstLeaf is not an integer");
			}
		}
		
		if(parameters.containsKey("optimizeMemory")){
			if(parameters.get("optimizeMemory").equals("false")){
				model.optimizeMemory = false;
				messageTrace("Mode standard");
			}else if (parameters.get("optimizeMemory").equals("true")){
				model.optimizeMemory = true;		
				messageTrace("Mode dataset pas chargé en RAM");		
			}else{
				error("invalid value for optimizeMemory. Must be true or false.");				
			}
		}
		
		if(parameters.containsKey("allTreesTogether")){
			if(parameters.get("allTreesTogether").equals("false")){
				model.allTreesTogether = false;
				messageTrace("Learning one tree after one tree...");
			}else if (parameters.get("allTreesTogether").equals("true")){
				model.allTreesTogether = true;				
				messageTrace("Learning all the trees at the same time...");
			}else{
				error("invalid value for allTreesTogether. Must be true or false.");				
			}
		}
		
		
		// running action
		
		CraftMLFileReader trainFile;
		CraftMLFileReader predictFile;
		CraftMLPredictionWriter predictionWriter;
		
		switch (action) {
		case "learnAndWrite":
			
			if(!checkFileExists(parameters.get("inputTrainFile"))) { 
				error("Train file not found");		
			}
			
			trainFile = createReader(parameters.get("inputFilesType"),parameters.get("inputTrainFile"),parameters);
			
			model.trainAlgoOnFile(trainFile);
			model.saveModel(parameters.get("outputPath"), parameters.get("modelName"));
			
			break;
		case "learnAndPredict":
			
			if(!checkFileExists(parameters.get("inputTrainFile"))) { 
				error("Train file not found");		
			}
			
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			trainFile = createReader(parameters.get("inputFilesType"),parameters.get("inputTrainFile"),parameters);
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			predictionWriter = new CraftMLPredictionWriter(parameters.get("outputPredictFile"));
			
			model.trainAlgoOnFile(trainFile);
			model.predictOnFile(predictFile, predictionWriter, topN);
			
			break;
		case "learnPredictAndMeasure":
			
			if(!checkFileExists(parameters.get("inputTrainFile"))) { 
				error("Train file not found");		
			}
			
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			trainFile = createReader(parameters.get("inputFilesType"),parameters.get("inputTrainFile"),parameters);
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			
			predictionWriter = new CraftMLPredictionWriter(parameters.get("outputPredictFile"));
			

			//TODO lire et interpreter les vraies metriques 
			rawMetrics = parameters.get("listOfMetrics");
			performances = new ArrayList<String>();			
			performances.add("Pat1");
			performances.add("Pat3");
			performances.add("Pat5");
			
			model.trainAlgoOnFile(trainFile);
			
			model.mesurePerformanceOnFile(predictFile, performances, true, parameters.get("outputMeasureFile"), true, predictionWriter, topN);
			
			break;
		case "learnAndMeasure":
			
			if(!checkFileExists(parameters.get("inputTrainFile"))) { 
				error("Train file not found");		
			}
			
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			trainFile = createReader(parameters.get("inputFilesType"),parameters.get("inputTrainFile"),parameters);
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			
			//TODO lire et interpreter les vraies metriques et top N à gerer en param
			rawMetrics = parameters.get("listOfMetrics");
			performances = new ArrayList<String>();			
			performances.add("Pat1");
			performances.add("Pat3");
			performances.add("Pat5");
			
			model.trainAlgoOnFile(trainFile);
			
			model.mesurePerformanceOnFile(predictFile, performances, true, parameters.get("outputMeasureFile"), false, null, topN);
			
			break;
		case "readModelAndPredict":
			//TODO check model path and name
						
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			predictionWriter = new CraftMLPredictionWriter(parameters.get("outputPredictFile"));
			
			model.loadModel(parameters.get("modelPath"), parameters.get("modelName"));
			
			model.predictOnFile(predictFile, predictionWriter, topN);
			
			
			break;
		case "readModelPredictAndMeasure":
			
			//TODO check model path and name
			
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			
			predictionWriter = new CraftMLPredictionWriter(parameters.get("outputPredictFile"));
			

			//TODO lire et interpreter les vraies metriques et top N à gerer en param
			rawMetrics = parameters.get("listOfMetrics");
			performances = new ArrayList<String>();			
			performances.add("Pat1");
			performances.add("Pat3");
			performances.add("Pat5");
			
			model.loadModel(parameters.get("modelPath"), parameters.get("modelName"));
				
			model.mesurePerformanceOnFile(predictFile, performances, true, parameters.get("outputMeasureFile"), true, predictionWriter, topN);
			
			break;
		case "readModelAndMeasure":
			
			//TODO check model path and name
			
			if(!checkFileExists(parameters.get("inputPredictFile"))) { 
				error("Predict file not found");		
			}
			
			predictFile = createReader(parameters.get("inputFilesType"),parameters.get("inputPredictFile"),parameters);
			
			//TODO lire et interpreter les vraies metriques et top N à gerer en param
			rawMetrics = parameters.get("listOfMetrics");
			performances = new ArrayList<String>();			
			performances.add("Pat1");
			performances.add("Pat3");
			performances.add("Pat5");
			
			model.loadModel(parameters.get("modelPath"), parameters.get("modelName"));
				
			model.mesurePerformanceOnFile(predictFile, performances, true, parameters.get("outputMeasureFile"), false, null, 20);
			break;
		default:
			error("Invalid action : " + action);
			break;
		}
		
	}
	
	
	public boolean checkFileExists(String fileName) {
		File f = new File(fileName);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		}else{
			return false;
		}
	}
	

	public CraftMLFileReader createReader(String fileType, String fileName,TreeMap<String, String> parameters) {
		CraftMLFileReader out = null;
		switch (fileType) {
		case "libsvm":
			out = new LibsvmFileReader();			
			out.setFile(fileName);
			break;
		case "tabular":
			
			TabularFileReader reader = new TabularFileReader();
			
			if(parameters.get("tabularFieldDelim").equals("TABULATION")) {
				reader.inputFieldSeparator = "\t";
			}else if (parameters.get("tabularFieldDelim").equals("SPACE")){
				reader.inputFieldSeparator = " ";
			}else {
				//TODO interdire des tabulations invalides
				reader.inputFieldSeparator = parameters.get("tabularFieldDelim");
			}
			
			reader.targetPrefix =  parameters.get("tabularClassPrefix");
			
			out = reader;		
			out.setFile(fileName);
			
			break;
		default:
			error("Unknown file type");
			break;
		}
		return out;
	}


}

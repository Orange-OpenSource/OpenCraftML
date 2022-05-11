package textModule;

public class CraftML4Text_API_Test_LittleTrain {

	
	
	static String myDir="c:/CraftML_Muse/";
	
	
	
	
	
	static String myTrainFile="littleTrain.txt";
	static String myTestFile="littleTest.txt";
	
	
	
	static CraftML4Text_API myAPI;
	
	//static CraftML4Text_Params myParams;
	
	static public void test_learnModel(String globalPath) {
		 myAPI= new CraftML4Text_API();
		 //myParams= new CraftML4Text_Params();
		//myAPI.newModel();
		String messResult=myAPI.file_learnModelFromFile(globalPath);
		System.out.println(messResult);
	}
	
	static public void test_Process_Interact_complete(String globalPath) {
		test_learnModel(globalPath);
		myAPI.file_predictOnInteractiveFile(globalPath);
	}
	
	
	public static void main (String[] args) {
		
		//String globalTrainFilePath1=myDir1+myTrainFile1;
		//test_learnModel(globalTrainFilePath1);
		//test_Process_Interact_complete(globalTrainFilePath1);
		//System.exit(0);
		
		String pathTrain=myDir+myTrainFile;
		String pathTest=myDir+myTestFile;
		
		
		
		myAPI= new CraftML4Text_API();
		myAPI.file_learnModelFromFile(pathTrain);
		
		//myAPI.file_predictOnInteractiveFile(pathTest);
		//myAPI.file_predictOnInteractiveFile(pathTest);
		
		
		myAPI.file_eval_precision(pathTest);
		
	}
	
	
	
	
}

package textModule;

public class CraftML4Text_API_Test {

	
	
	static String myDir1="C:\\Users\\ofmu6285\\Desktop\\ABC_Interact\\";
	
	
	static String myDirQuestions="C:\\Users\\ofmu6285\\Desktop\\DATA_SONIA\\";
	
	//static String myTrainFile="DjingoLittle.txt";
	
	static String myTrainFile1="littleA.txt";
	
	
	static String myTrainFileQuestion="Question_NonQuestion_Apprentissage_V3_CraftML.txt";
	static String myTestFileQuestion="Question_NonQuestion_Test_V3_CraftML.txt";
	
	static String myTestFileQuestionSansY="Question_NonQuestion_Test_SANSY_V3_CraftML.txt";
	
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
		
		String pathQuestionTrain=myDirQuestions+myTrainFileQuestion;
		String pathQuestionTest=myDirQuestions+myTestFileQuestion;
		
		String pathQuestionTestSansY=myDirQuestions+myTestFileQuestionSansY;
		
		myAPI= new CraftML4Text_API();
		myAPI.file_learnModelFromFile(pathQuestionTrain);
		myAPI.file_predictOnInteractiveFile(pathQuestionTest);
		
		myAPI.file_predictOnInteractiveFile(pathQuestionTestSansY);
		
		
		myAPI.file_eval_precision(pathQuestionTest);
		
	}
	
	
	
	
}

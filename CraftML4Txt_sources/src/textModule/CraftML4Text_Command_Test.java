package textModule;

public class CraftML4Text_Command_Test {

	
	static String myDir="C:\\Users\\ofmu6285\\Desktop\\ABC_Interact\\";
	
	static String myDirForModels="C:\\Users\\ofmu6285\\Desktop\\ABC_Interact\\models\\";
	
	//static String myTrainFile="DjingoLittle.txt";
	
	static String myTrainFile="littleA.txt";
	
	
	
	public static void main (String[] params) {
		CraftML4Text_Command myInterpretor= new CraftML4Text_Command();
		String cmd="load model c:/myModels/model1";
		String param=CraftML4Text_Command.getParamAfterThat(cmd, "load model");
		System.out.println("["+param+"]");
		
		myInterpretor.commandInterpretor("learn "+myDir+myTrainFile);
		
		myInterpretor.commandInterpretor("save "+myDirForModels+"modelLittleA");
		
		myInterpretor.myAPi.myModel=null ; // destroying the model
		
		myInterpretor.commandInterpretor("load "+myDirForModels+"modelLittleA");
		
		myInterpretor.commandInterpretor("predict "+myDir+myTrainFile);
		
		
	}
	
	
}

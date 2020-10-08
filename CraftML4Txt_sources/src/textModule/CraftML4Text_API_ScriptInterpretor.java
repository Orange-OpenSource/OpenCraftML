package textModule;

import utils.MessManager;

public class CraftML4Text_API_ScriptInterpretor {
	
	
	
	//static String dir="C:\\\\Users\\\\ofmu6285\\\\Desktop\\\\ABC_Interact\\";
	static String dir="C:\\testCraftML4Text\\";
	
	//static String scriptFile="script_QuestionNoQuestion.txt";
	
	static String scriptFile="ScriptGeneralNonRegressionMai2020.txt";
	

	CraftML4Text_Command myInterpretor= new CraftML4Text_Command();
	
	public  String interpretor(String scriptGlobalPath) {
		MessManager.say("Script interpretor running for: "+scriptGlobalPath);
		String s="";
		SimpleTextReaderUTF8 myReader = new SimpleTextReaderUTF8();
		if (!CraftML4Text_Command.isOpenable(scriptGlobalPath))  {
			s="Script interpretor Error: can't open "+scriptGlobalPath;
			MessManager.say(s);
			return s;
		}
		myReader.openFile(scriptGlobalPath);
		String commandLine=myReader.readLine();
		if (commandLine==null) {
			s="Script file is empty :"+scriptGlobalPath;
			return s;
		} 
		boolean ok=true;
		int warning =0;
		while (ok) {
			commandLine=commandLine.trim();
			MessManager.say("executing: "+commandLine);
			String clow=commandLine.toLowerCase();
			if (! (      (clow.startsWith("//")||(clow.equals("stop"))||clow.equals("") )  ) ) {
				s=myInterpretor.commandInterpretor(commandLine);
				String sp=s.toLowerCase();
				if (sp.startsWith("error")) {
					//ok=false;
					warning++;
					System.out.println();
					System.out.println("warning: "+warning);
					System.out.println("error for line :");
					System.out.println(commandLine);
					System.out.println("check syntax or check if text is true UTF8  format");
					if (warning>3) {
						System.out.println("too much warning, aborting");
						ok=false;
					}
					
				}
				MessManager.say(s);
			}
			if (clow.startsWith("stop")) {
				ok=false;
			}
			commandLine=myReader.readLine();
			if (commandLine==null) {
				ok=false;
			}
			
		}
		if (warning>0) {
			return "End Exec - number of warning: "+warning;
		}
		return "End Exec - status OK";
	}
	
	public static void main (String[] params) {
		
		System.out.println(CraftML4Text_Params.infoVersion);
		String scriptGlobalPath=dir+scriptFile;
		
		if (params!=null) {
			if (params.length!=0) {
				scriptGlobalPath=params[0];
			} else {
				System.out.println("no parameter found: going to use default script");
			}
			
		} else {
			System.out.println("no parameter found: going to use default script");
		}
		
		
		
		
		System.out.println("CraftML4txt: using scriptFile: "+scriptGlobalPath);
		
		CraftML4Text_API_ScriptInterpretor myInterpretor= new CraftML4Text_API_ScriptInterpretor();
		myInterpretor.interpretor(scriptGlobalPath);
		
		
		
	}
	
	
	
	
}

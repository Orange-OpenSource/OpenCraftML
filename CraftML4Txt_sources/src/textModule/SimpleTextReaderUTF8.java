package textModule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JPopupMenu.Separator;
/**
 * Classe de lecture d'un fichier texte codé au format UTF8
 * @author ofmu6285
 *
 */

public class SimpleTextReaderUTF8 {

	long nbLineRead=0;
	
	static String mainSourceFolder="C:/miniVipe_EtDataSets/dataAvis4g/";
	
	static String myFile="corpus4G_tagSuper_BaseAPPRENTISSAGE_10000_UTF8.txt";

	public String defaultFileGlobalPath;
	
	BufferedReader reader = null;
		
	public boolean traceMode=false;
			
	public boolean openDefaultFile() {
		boolean ok=openFile(defaultFileGlobalPath);
		return ok;
	}

	

	
	
	/**
	 * essaie d'ourvir le fichier ; 
	 * @param filename
	 * @return true if openable, false otherwise
	 */
	public boolean openFile(String filename)
	{
		
		System.out.println("Opening:"+filename);
		try
		{
			//reader = new BufferedReader(new FileReader(filename));
			
			reader = new  BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			
			
			nbLineRead=0;
			return true;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;
	}




	public void closeFile()
	{
		if(reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String readLine()
	{
		try
		{
			String line;
			//System.out.println("va lire...");
			if ((line = reader.readLine()) != null)
			{
				
				nbLineRead++;
				if (traceMode) {
					System.out.println(nbLineRead+"  :["+line+"]");
				}
				
				return line;
			}
			reader.close();    
		}
		catch (Exception e)
		{
				e.printStackTrace();
		}
		return null;
	}

	
	
	
	static public void printRecord(String[] record,String separatorOut) {
		if (record!=null) {
			for (int i=0; i<record.length;i++) {
				System.out.print(record[i]);
				System.out.print(separatorOut);
			}
		}
		System.out.println();
	}

	
	

	public static void main(String[] args) {

		//String myFile="ohsumedy.txt";
		
		String filePath=mainSourceFolder+myFile;
		
		SimpleTextReaderUTF8 myReader=new SimpleTextReaderUTF8();

		
		myReader.openFile(filePath);
		
		String myLines;
		myLines=myReader.readLine();
		int nb=0;
		while (myLines!=null) {
			nb++;
			System.out.println(nb+"  "+myLines);
			myLines=myReader.readLine();
		}
		System.out.println("nb line(s) read: "+myReader.nbLineRead);
		
	}

}


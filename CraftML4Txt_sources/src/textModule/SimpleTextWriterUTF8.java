package textModule;

/**
 * Classe d'écriture de fichiers textes au format UTF8
 */


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SimpleTextWriterUTF8 {
	
	
	
	static String myDir="c:";
	
	static String myFileName="toto.txt";

	//FileWriter writer = null;
	
	BufferedWriter writer;
	
	int numberLineWritten=0;
	
	String globalName;
	
	public boolean traceMode=false;
	
	public boolean openFile(String filename)
	{
		globalName=filename;
		
		try
		{
			
			 writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			
			//writer = new FileWriter(filename); 
			return true;
		}
		catch (Exception e)
		{
			
				System.err.format("Exception occurred trying to open '%s'.", filename);
				return false;
			
		}

		
	}

	/**
	 * close the txt log file
	 */
	public void closeFile()
	{
		if(writer != null) {
			try {
				writer.close();
				System.out.println(globalName+": number line written: "+numberLineWritten);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("PROBLEM CLOSING: "+globalName);
			}
		}

	}

	/**
	 * write a record 
	 * @param line
	 * @return
	 */
	public boolean writeRecord(String line)
	{
		try {
			
			
				writer.write(line);
				if (traceMode) {
					System.out.println("writing: "+line);
					
				}
			
			writer.write("\n");
			numberLineWritten++;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public static void main (String[] p) {
		
		if (!myDir.endsWith("/")) {
			myDir=myDir+"/";
		}
		String myGlobalPath=myDir+myFileName;
		
		SimpleTextWriterUTF8 myWriter = new SimpleTextWriterUTF8();
		
		myWriter.openFile(myGlobalPath);
		myWriter.writeRecord("Hello word UTF8 !");
		myWriter.closeFile();
		
	}
	
	

	
}

package FilesManagement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class RecordTextWriter {

	FileWriter writer = null;
	String currentFile;
	
	public String separator = ";";
	
	public void setSeparator(String newSeparator){
		this.separator = newSeparator;
	}
	
	public String getFileName() {
		return currentFile;
	}

	public boolean openFile(String filename)
	{
		currentFile = filename;
		//separator = lseparator;
		File myfile = new File(filename);
		myfile.getParentFile().mkdirs();

		try
		{
			writer = new FileWriter(filename); 
			return true;
		}
		catch (Exception e)
		{
			//if(error) {
				System.err.format("Exception occurred trying to open '%s'.", filename);
				e.printStackTrace();
			//}
		}

		return false;
	}

	public void closeFile()
	{
		if(writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean writeRecord(String[] line)
	{
		try {
			
			for(int i=0;i<line.length;i++)
			{
				writer.write(line[i]);
				if (i<(line.length-1)) {
					writer.write(separator);
				}
			}
			writer.write("\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean writeRecord(float[] line)
	{
		try {
			
			for(int i=0;i<line.length;i++)
			{
				writer.write(Float.toString(line[i]));
				if (i<(line.length-1)) {
					writer.write(separator);
				}
			}
			writer.write("\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean writeRecord(long[] line)
	{
		try {
			
			for(int i=0;i<line.length;i++)
			{
				writer.write(Long.toString(line[i]));
				if (i<(line.length-1)) {
					writer.write(separator);
				}
			}
			writer.write("\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean writeLine(String line){
		try {			
			writer.write(line);
			writer.write("\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Voir main du LogTextReader ....

	}
}
package FilesManagement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.sound.sampled.Line;

import Algorithm.Displayer;




public class RecordTextReader {

	long nbReadLine=0;
	
	static String mainSourceFolder="c:/testDicos/";

	BufferedReader reader = null;
	public String separatorInText = " ";
	String separatorRecord ="\t";
	
	public boolean fistColumnIsID=false;
	
	public boolean cleanLineOption=true;
	
	public boolean lowerCase=true;
	public boolean upperCase=false;
			
	public boolean recordMode=true;
	
	public boolean tryMode=false;
	
	public boolean secondFieldIsText=true;
	
	
	public boolean deleteAfterSlash=true;
	

			
	public String cleanText(String myText) {

		 {
			//line=line.trim();
			if (lowerCase) {
				myText=myText.toLowerCase();   
			}
			if (upperCase) {
				myText=myText.toUpperCase();   
				
			}
			myText=myText.replaceAll("\t", " ");
			myText=myText.replace("(", " ");
			myText=myText.replace(")", " ");
			myText=myText.replace(",", " ");
			myText=myText.replace(";", " ");
			myText=myText.replace(".", " ");    //TODO : vérifier l'expresseion régulière pour le point
			myText=myText.replaceAll("    ", " ");
			myText=myText.replaceAll("   ", " ");
			myText=myText.replaceAll("  ", " ");
			//line=line.replace(commandTag2, commandTag1);
			
			if (deleteAfterSlash) {
				int p;
				p=myText.indexOf("/");
				if (p>0) {  // pas retiré si en début de chaine, au cas où
					//System.out.println("TEXT:"+myText);
					myText=myText.substring(0, p);
				}
				//System.out.println("TEXT:"+myText);
				//System.exit(1);
			}
			
			
			myText=myText.trim();
			return myText;
		}
	}
			
			

	public String getSeparatorRecord() {
		return separatorRecord;
	}



	public void setSeparatorRecord(String separatorRecord) {
		this.separatorRecord = separatorRecord;
	}



	public boolean openFile(String filename)
	{
		
		System.out.println("\n Opening file: "+filename + "\n");
		
		
		/*
		System.out.println("ID in 1st column:"+fistColumnIsID);
		if (fistColumnIsID) {
			System.out.println("record separator:["+separatorRecord+"]");
		}
		System.out.println("with text separator:["+separatorInText+"]");
		System.out.println("cleaning :"+cleanLineOption);
		if (cleanLineOption) {
			if (lowerCase) {
			  System.out.println("     to lowercase");
			}
			if (upperCase) {
				System.out.println("    to upperCase");
			}
		}
		
		*/
		
		try
		{
			reader = new BufferedReader(new FileReader(filename));
			nbReadLine=0;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
	
	
	public String readLine(){
		try
		{
			String line;
			//System.out.println("va lire...");
			if ((line = reader.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
				e.printStackTrace();
		}
		return null;
	}
	

	public String[] readPureRecord()
	{
		try
		{
			String line;
			//System.out.println("va lire...");
			if ((line = reader.readLine()) != null)
			{
				
				nbReadLine++;
				if (tryMode) {
					if (nbReadLine>5) {
						Displayer.displayText("TRY MODE: exit");
						reader.close();
						return null;
					}
				}
				
				//Displayer.displayText("ligne lue:["+line+"]");
				
				
				String[] record;
				record=line.split(separatorRecord);
				
				/*
				 Iterable<String> cutLine = Splitter.on(separatorRecord).split(line);				
				String[] record = Iterables.toArray(cutLine, String.class);
				 */
				
				return record;
			}
			reader.close();    
		}
		catch (Exception e)
		{
				e.printStackTrace();
		}
		return null;
	}

	
	public String[] readLineOrRecord()
	{
		try
		{
			String line;
			//System.out.println("va lire...");
			if ((line = reader.readLine()) != null)
			{
				
				nbReadLine++;
				if (tryMode) {
					if (nbReadLine>5) {
						Displayer.displayText("TRY MODE: exit");
						reader.close();
						return null;
					}
				}
				
				//Displayer.displayText("ligne lue:["+line+"]");
				
				
				String[] record;
				if (recordMode) {
					if (!fistColumnIsID) {
						record= line.split(separatorInText);
					} else {
						String[] idAndText=line.split(separatorRecord);
						//if (cleanLineOption) {
						//	idAndText[1]=cleanLine(idAndText[1]);
						//	System.out.println("ligne 'cleanée' :["+idAndText[1]+"]");
						//}
						String[] textRecord;
						if (idAndText.length>1) {
							 textRecord=idAndText[1].split(separatorInText);
						} else {
							textRecord=new String[1];
							textRecord[0]="";
						}
						if (cleanLineOption) {
							for (int i=0;i<textRecord.length;i++) {
								textRecord[i]=cleanText(textRecord[i]);
							}
						}
						record=new String[textRecord.length+1];
						record[0]=idAndText[0];
						for (int i=0;i<textRecord.length;i++) {
							record[i+1]=textRecord[i];
						}
					}
					
				} else {
					record=new String[1];
					if (cleanLineOption) {
						line=cleanText(line);
						//Displayer.displayText("ligne 'cleanée' :["+line+"]");
					}
					record[0]=line;
				}
				return record;
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
				//Displayer.displayText(record[i]);
				//Displayer.displayText(separatorOut);
			}
		}
		//Displayer.displayText("");
	}

	
	

	public static void main(String[] args) {
		/*
		String myFile="corpus4G_tagSuper_BaseAPP10000.txt";
		//String myFile="ohsumedy.txt";
		
		String filePath=mainSourceFolder+myFile;
		
		
		
		
		RecordTextReader myReader=new RecordTextReader();
		myReader.cleanLineOption=true;
		//myReader.upperCase=true;
		//myReader.recordMode=true;
		//myReader.fistColumnIsID=true;
		myReader.tryMode=true;
		myReader.separatorInText=" ";
		myReader.separatorRecord="\t";
		
		
		
		myReader.openFile(filePath);
		
		String[] myRecords;
		myRecords=myReader.readLineOrRecord();
		while (myRecords!=null) {
			printRecord(myRecords,";");
			Displayer.displayText("");
			myRecords=myReader.readLineOrRecord();
			
			
		}*/
		
		String path = "C:/datachallenge/properties_2016_processed.csv";
		
		//String pathout = "C:/datachallenge/properties_2016_processed.csv";
		
		RecordTextReader mReader = new RecordTextReader();
		
		//RecordTextWriter mWriter = new RecordTextWriter();
		//mWriter.setSeparator("\t");
		//mWriter.openFile(pathout);
		
		mReader.openFile(path);
		
		
		String line = mReader.readLine();
		Displayer.displayText(line);
		int i = 0;
		String[] splittedLine;
		String sep = ",";
		while(line != null){
			i++;	
			//splittedLine = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);	
			//splittedLine = line.split("\t", -1);	
			if(i>500 && i < 800){
				Displayer.displayText(line);
			}
			line = mReader.readLine();
		}
		Displayer.displayText(Integer.toString(i));
		mReader.closeFile();

	}

}


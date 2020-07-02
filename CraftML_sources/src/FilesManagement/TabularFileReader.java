package FilesManagement;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Algorithm.Displayer;

/**
 * 
 * 
 * 
 * @author OFMU6285
 *
 */

public class TabularFileReader implements CraftMLFileReader {

	public String targetPrefix="class"; // rem : pour les tests, on pourra mettre "1" pour mettre en target les variables commençant par "1"...


	String[] header;

	boolean[] headerTargetIndicator;

	int headerSize;

	 public String getInputFieldSeparator() {
		return inputFieldSeparator;
	}

	public void setInputFieldSeparator(String inputFieldSeparator) {
		this.inputFieldSeparator = inputFieldSeparator;
	}


	public String inputFieldSeparator = "\t";

	SmallItem currentX;
	SmallItem currentY;

	RecordTextReader myRecordReader = null;

	String filePath = null;


	String[] nextRecord;


	//ArrayList<String[]> myListOfLogs;



	long nbRecord;





	public void error_fatal(String mess) {
		display("fatal error: "+mess);
		Error er = new Error(mess);
		throw er;	
	}

	public void display(String mess) {
		Displayer.displayText(mess);
	}

	public boolean isObjectOrFeatureNameOK(String s) {
		if (s.contains(";")) return false;
		if (s.contains(",")) return false;
		if (s.contains("\t")) return false;
		if (s.contains(" ")) return false;
		if (s.length()>=250) return false;
		return true;

	}


	public void printContext(String[] record) {
		String line="";
		for (int j=0;j<record.length;j++) {
			line=line+record[j]+" ";
		}
		display("line number "+nbRecord);
		display(line);
	}
	
	
	public int countLines() {
	    InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        try {
				while ((readChars = is.read(c)) != -1) {
				    empty = false;
				    for (int i = 0; i < readChars; ++i) {
				        if (c[i] == '\n') {
				            ++count;
				        }
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	
	String[] getNormalizedCleanedFields() {
	
		String[] result=myRecordReader.readPureRecord();
		if (result!=null) {
			for (int i=0;i<result.length;i++) {
				result[i]=result[i].trim();
				if (!isObjectOrFeatureNameOK(result[i])) {
					printContext(result);
					display("wrong field containing bad character space tab comma or semicolom <"+result[i]+">");
					error_fatal("wrong field containing bad character space tab comma or semicolom <"+result[i]+">");
				}
			}
			if (nbRecord>0 && result.length!=headerSize) {
				printContext(result);
				display("wrong record : expected "+headerSize+" records, number of fields is: "+result.length);
				error_fatal("error");
			} 
		}
		
		return result;
	}


	@Override
	public void setFile(String filePath) {
		this.filePath = filePath;

	}
	
	public int getNbVariables() {
		RecordTextReader mReader = new RecordTextReader();
		mReader.setSeparatorRecord(inputFieldSeparator);
		mReader.openFile(filePath);
		String [] header = mReader.readPureRecord();
		
		if (header!=null) {
			for (int i=0;i<header.length;i++) {
				header[i]=header[i].trim();
				if (!isObjectOrFeatureNameOK(header[i])) {
					printContext(header);
					display("wrong field containing bad character space tab comma or semicolom <"+header[i]+">");
					error_fatal("error");
				}
			}
			if (nbRecord>0 && header.length!=headerSize) {
				printContext(header);
				display("wrong record : expected "+headerSize+" records, number of fields is: "+header.length);
				error_fatal("error");
			} 
		}else {
			display("Wrong header, null");
			error_fatal("error");
		}
		return header.length - 1;
	}
	

	@Override
	public void openFile() {
		// initializing reader
		myRecordReader = new RecordTextReader();
		myRecordReader.setSeparatorRecord(inputFieldSeparator);
		myRecordReader.openFile(filePath);
		// reading header
		header=getNormalizedCleanedFields();
		if (header==null) {
			display("Wrong header, null");
			error_fatal("error");
		}
		headerSize=header.length;
		display("number of recognized fields: "+headerSize);
		if (headerSize<1) {
			display("Wrong header, null");
			error_fatal("error");
		}
		headerTargetIndicator = new boolean[headerSize];
		// détermination des variables target et non target
		for (int i=0; i<headerSize;i++) {
			if (header[i].startsWith(targetPrefix)) {
				headerTargetIndicator[i]=true;
				display(i+" target recognized: "+header[i]);
			} else {
				headerTargetIndicator[i]=false;
				display(i+" feature recognized: "+header[i]);
			}
		}
		display("");
	}

	@Override
	public boolean isReady(){
		return filePath != null;
	}


	@Override
	public boolean readNext() {
		nbRecord++;
		nextRecord=getNormalizedCleanedFields();
		if (nextRecord==null) {
			return false;
		} else {

			return true;	
		}
	}		

	/**
	 * Ajoute une valeur si elle est numérique, ou sinon concatène la valeur symbolique au nom de la variable et associe une valeur 1
	 * @param currentItem
	 * @param featureName
	 * @param valueFromMatrix
	 */
	public void addAttributeValueNumericOrSymbolic(SmallItem currentItem, String featureName, String valueFromMatrix) {
		
		Float value=1f;
		try {
			value=Float.parseFloat(valueFromMatrix); 
		} catch (NumberFormatException e) {
			featureName=featureName+"_"+valueFromMatrix;
		}
		currentItem.putKeyValue(featureName, value);
		
	}
	
	/*
	public void addLabelValueSymbolic(SmallItem currentItem, String featureName, String valueFromMatrix) {
		
		Float value=1f;
		featureName=featureName+"_"+valueFromMatrix;
		currentItem.putKeyValue(featureName, value);
		
	}*/
	


	@Override
	public SmallItem getX() {
		if (nextRecord==null) {
			display("wrong call to getX: nextRecord was null");
			return null;
		}
		SmallItem myResultX=new SmallItem();
		for (int i=0;i<nextRecord.length;i++) {
			if (!headerTargetIndicator[i]) {
				addAttributeValueNumericOrSymbolic(myResultX, header[i], nextRecord[i]);
			}
		}
		return myResultX;
	}

	@Override
	public SmallItem getY() {
		if (nextRecord==null) {
			display("wrong call to getY: nextRecord was null");
			return null;
		}
		SmallItem myResultY=new SmallItem();
		for (int i=0;i<nextRecord.length;i++) {
			if (headerTargetIndicator[i]) {
				addAttributeValueNumericOrSymbolic(myResultY, header[i], nextRecord[i]);
				//addLabelValueSymbolic(myResultY, header[i], nextRecord[i]);
			}
		}
		return myResultY;
	}

	@Override
	public void closeFile() {
		// TODO Auto-generated method stub

	}


	//--------------------------------------------------------






	public static void main(String[] p) {
		//String directory="C:\\MovieLens1M_formatsPivot\\";
		String directory="C:\\benchWissam2016_2017\\bench_multiclasse\\primary-tumor\\out\\";
		String fileName="primary-tumor_train1.txt";
		//String fileName="adult_UCI.txt";

		//String fileName="ratingML.txt";
		String path=directory+fileName;
		Displayer.displayText("file path: "+path);
		long countx=0;
		long county=0;
		TabularFileReader myTableReader=new TabularFileReader();
		myTableReader.setFile(path);
		myTableReader.openFile();
		while (myTableReader.readNext()) {
			Displayer.displayText("record n°"+myTableReader.nbRecord);
			SmallItem xx=myTableReader.getX();
			SmallItem yy=myTableReader.getY();
			if (xx.getSize()>0) {
				countx++;
				Displayer.displayText("X= "+xx.getLinetext(" ", "="));
			}
			if (yy.getSize()>0) {
				county++;
				Displayer.displayText("Y= "+yy.getLinetext(" ", "="));
			}
			Displayer.displayText("");
		
		}


		

	}

}


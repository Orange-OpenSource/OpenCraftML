package FilesManagement;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LibsvmFileReader implements CraftMLFileReader {
	
	boolean header = true;
	
	String inputFieldSeparator = " ";
	String inputLabelSeparator = ",";
	String inputKeyValueSeparator = ":";
	
	SmallItem currentX;
	SmallItem currentY;
	
	RecordTextReader myRecordReader = null;
	
	String filePath = null;

	@Override
	public void setFile(String filePath) {
		this.filePath = filePath;
		
	}

	@Override
	public void openFile() {
		// initializing reader
		myRecordReader = new RecordTextReader();
		myRecordReader.setSeparatorRecord(inputFieldSeparator);

		

		myRecordReader.openFile(filePath);

		// reading header
		if(header){
			myRecordReader.readPureRecord();
		}		

	}
	
	@Override
	public boolean isReady(){
		return filePath != null;
	}

	@Override
	public boolean readNext() {
		String[] line, lineY, valX;
		currentX = new SmallItem();
		currentY = new SmallItem();
		line = myRecordReader.readPureRecord();
		if(line != null){
			if (!line[0].equals("")) {
				lineY = line[0].split(inputLabelSeparator);
				for (int j = 0; j < lineY.length; j++) {
					currentY.putKeyValue(lineY[j], 1f);
				}
			}
			
			for (int j = 0; j < (line.length - 1); j++) {
				valX = line[j + 1].split(inputKeyValueSeparator);
				currentX.putKeyValue(valX[0], Float.parseFloat(valX[1]));
			}
			
			return true;
		}else{
			return false;
		}		
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
	

	@Override
	public SmallItem getX() {
		return currentX;
	}

	@Override
	public SmallItem getY() {
		return currentY;
	}
	
	@Override
	public void closeFile(){
		myRecordReader.closeFile();
	}



	

}

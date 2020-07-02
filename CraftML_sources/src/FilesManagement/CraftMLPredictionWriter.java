package FilesManagement;

public class CraftMLPredictionWriter {
	String fileName;
	RecordTextWriter mWriter;

	public CraftMLPredictionWriter(String fileName) {
		super();
		this.fileName = fileName;
		mWriter = new RecordTextWriter();
		mWriter.openFile(fileName);
	}
	
	public void writePrediction(SmallItem x,SmallItem ypred) {
		//String lineToWrite = x.getLinetext(";", "=") + "\t" + ypred.getLinetext(";", "=");
		String lineToWrite = ypred.getLinetext(";", "=");
		mWriter.writeLine(lineToWrite);
	}
	
	public void close(){
		mWriter.closeFile();
	}
	
}

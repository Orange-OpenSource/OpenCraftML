package FilesManagement;

public interface CraftMLFileReader {
	
	public void setFile(String filePath);
	public void openFile();
	public boolean isReady();
	public boolean readNext();
	public SmallItem getX();
	public SmallItem getY();
	public void closeFile();
	public int countLines();
	
}

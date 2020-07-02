package Algorithm;

import java.util.ArrayList;

import FilesManagement.RecordTextReader;
import FilesManagement.RecordTextWriter;
import FilesManagement.SmallItem;

public interface GenericNodeCraftML {
	
	public void learn(SmallItem x, SmallItem y);
	public void showStats();
	public void learn(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY,int compteurIncident);
	public GenericNodeCraftML getLeaf(SmallItem x);
	public void addPath(SmallItem x, ArrayList<GenericNodeCraftML> nodes);
	public void addNativeInformation(NativeInformation nativeInformation);
	public ArrayList<NativeInformation> getNativeInformation();
	public SmallItem predict(SmallItem x);
	public void restoreNode(RecordTextReader mRecordTextReader, String[] record);
	public void storeNode(RecordTextWriter file,float[] instanceValues,int[] instanceIndexes);
	public int getNbNode();
	public int getNbLeaf();
	public int getNbInstanceLeaves();
	public int getMaxInstanceLeaf();
	public int getMinInstanceLeaf();
	public int sumDepthLeaf();
	public int getMaxDepthLeaf();
	public int getMinDepthLeaf();
	public boolean indicationFinDePasse();
	public boolean isTrained();
	public String getID();	
	public GenericNodeCraftML[] getChildren();
	public String getLabelsPrediction();
	public void setLabelsPrediction(String labelsPrediction);
	public void setNbInstance(int nbInstance);
	public int getNbInstance();
	
}

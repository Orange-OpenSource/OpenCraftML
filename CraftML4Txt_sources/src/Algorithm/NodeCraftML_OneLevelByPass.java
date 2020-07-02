package Algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import FilesManagement.RecordTextReader;
import FilesManagement.RecordTextWriter;
import FilesManagement.SmallItem;

/**
 * Class for a tree/node of CraftML.
 * @author XPGT4620
 *
 */
public class NodeCraftML_OneLevelByPass implements GenericNodeCraftML {


	boolean hasLearnt;

	int floor;
	String id;

	int nbInstance;

	public int getNbInstance() {
		return nbInstance;
	}

	public void setNbInstance(int nbInstance) {
		this.nbInstance = nbInstance;
	}



	int seedSignX;
	int seedSignY;

	int seedIndexX;
	int seedIndexY;

	boolean checkSameX = true;
	boolean checkSameY = true;

	boolean otherProjection = false;

	boolean isLeaf = false;

	int minInst = 10;

	List<float[]> childrenInstancesValues;
	List<int[]> childrenInstancesIndex;

	ArrayList<NodeCraftML_OneLevelByPass> children;

	int branchFactor = 10;

	int depthMax = 800;

	int sizeReservoirKmeans = 20000;
	int sizeReservoirNativeInformation = 10;

	int dimXProj;
	int dimYProj;

	String labelsPrediction;

	public String getLabelsPrediction() {
		return labelsPrediction;
	}

	public void setLabelsPrediction(String labelsPrediction) {
		this.labelsPrediction = labelsPrediction;
	}

	public void messageTrace(String message) {
		Displayer.displayText(message);
	}



	SmallItem xCheck;
	SmallItem yCheck;

	int sparsity;

	String mode;

	ArrayList<NativeInformation> nodeNativeInformation = null;

	int nbNativeInformation = 0;


	ArrayList<float[]> reservoirValuesX;
	ArrayList<String[]> reservoirKeyX;
	ArrayList<float[]> reservoirValuesY;
	ArrayList<String[]> reservoirKeyY;


	public String getID(){
		return id;
	}

	public NodeCraftML_OneLevelByPass(int floor, String id, int dimXProj,int dimYProj,int seedIndexX,int seedSignX,int seedIndexY,int seedSignY,int sparsity,String mode,int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		super();

		this.minInst = minInst;
		this.sparsity = sparsity;
		this.floor = floor;
		this.id = id;
		this.dimXProj = dimXProj;
		this.dimYProj = dimYProj;

		this.mode = mode;

		this.branchFactor = branchFactor;
		this.sizeReservoirKmeans = sizeReservoir;
		this.depthMax = depthMax;

		Random random = new Random();

		if(mode.equals("SXSY")){

			this.seedIndexX = seedIndexX;
			this.seedSignX = seedSignX;

			this.seedIndexY = seedIndexY;
			this.seedSignY = seedSignY;

		}else if(mode.equals("DXDY")){

			this.seedIndexX = random.nextInt(Integer.MAX_VALUE);
			this.seedSignX = random.nextInt(Integer.MAX_VALUE);

			this.seedIndexY = random.nextInt(Integer.MAX_VALUE);
			this.seedSignY = random.nextInt(Integer.MAX_VALUE);

		}else if(mode.equals("SXDY")){

			this.seedIndexX = seedIndexX;
			this.seedSignX = seedSignX;

			this.seedIndexY = random.nextInt(Integer.MAX_VALUE);
			this.seedSignY = random.nextInt(Integer.MAX_VALUE);

		}else if(mode.equals("DXSY")){

			this.seedIndexX = random.nextInt(Integer.MAX_VALUE);
			this.seedSignX = random.nextInt(Integer.MAX_VALUE);

			this.seedIndexY = seedIndexY;
			this.seedSignY = seedSignY;

		}else{
			error("Mode invalide");
		}



		this.nbInstance = 0;
		this.labelsPrediction = null;
		this.hasLearnt = false;
		this.reservoirKeyX = new ArrayList<String[]>();
		this.reservoirKeyY = new ArrayList<String[]>();
		this.reservoirValuesX = new ArrayList<float[]>();
		this.reservoirValuesY = new ArrayList<float[]>();

		this.otherProjection = otherProjection;

	}



	public NodeCraftML_OneLevelByPass(int floor, String id, int dimXProj,int dimYProj,int sparsity,String mode, int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		super();

		this.minInst = minInst;

		this.sparsity = sparsity;
		this.floor = floor;
		this.id = id;
		this.dimXProj = dimXProj;
		this.dimYProj = dimYProj;

		this.mode = mode;

		this.branchFactor = branchFactor;
		this.sizeReservoirKmeans = sizeReservoir;
		this.depthMax = depthMax;

		Random random = new Random();

		this.seedIndexX = random.nextInt(Integer.MAX_VALUE);
		this.seedSignX = random.nextInt(Integer.MAX_VALUE);

		this.seedIndexY = random.nextInt(Integer.MAX_VALUE);
		this.seedSignY = random.nextInt(Integer.MAX_VALUE);

		this.nbInstance = 0;
		this.labelsPrediction = null;
		this.hasLearnt = false;
		this.reservoirKeyX = new ArrayList<String[]>();
		this.reservoirKeyY = new ArrayList<String[]>();
		this.reservoirValuesX = new ArrayList<float[]>();
		this.reservoirValuesY = new ArrayList<float[]>();


		this.otherProjection = otherProjection;

	}


	public void restoreNode(RecordTextReader file,String[] record) {


		if(floor != Integer.parseInt(record[0])) {
			error("incoherence �tage");
		}


		id = record[1];


		isLeaf = record[3].equals("leaf");

		nbInstance = Integer.parseInt(record[7]);

		seedIndexX = Integer.parseInt(record[8]);
		seedSignX = Integer.parseInt(record[9]);
		seedIndexY = Integer.parseInt(record[10]);
		seedSignY = Integer.parseInt(record[11]);


		if(isLeaf) {
			labelsPrediction = record[5];
		}else {

			children = new ArrayList<NodeCraftML_OneLevelByPass>();
			childrenInstancesValues = new ArrayList<float[]>();
			childrenInstancesIndex = new ArrayList<int[]>();

			int realNbChildren = Integer.parseInt(record[4]);

			String[] newRecord;
			float[] instanceValues;
			int[] instanceIndexes;

			NodeCraftML_OneLevelByPass currentChild;

			for(int i = 0;i<realNbChildren;i++) {

				newRecord = file.readPureRecord();

				if(newRecord.length!=12) {
					error("enregistrement de la mauvaise taille");
				}else {

					if(newRecord[6].equals("none")){
						instanceValues = new float[0];
						instanceIndexes = new int[0];
						currentChild = (NodeCraftML_OneLevelByPass) generateNodeWithoutSeeds(floor + 1, "t_t", dimXProj, dimYProj, sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
						currentChild.restoreNode(file, newRecord);
						children.add(currentChild);
						childrenInstancesIndex.add(instanceIndexes);
						childrenInstancesValues.add(instanceValues);
					}else{
						String[] instanceString = newRecord[6].split(";");
						instanceValues = new float[instanceString.length];
						instanceIndexes = new int[instanceString.length];
						if(instanceString.length > dimXProj) {
							error("incoh�rence dans la taille du s�parateur");
						}else {
							for(int j = 0;j<instanceString.length;j++) {
								String[] spl = instanceString[j].split("=");
								if(spl.length == 2){
									int currIndex = Integer.parseInt(spl[0]);
									if(currIndex > dimXProj){
										error("incoh�rence dans la taille du s�parateur");
									}else{
										instanceIndexes[j] = currIndex;
										instanceValues[j] = Float.parseFloat(spl[1]);
									}
								}else{
									error("probl�me dans le format du s�parateur");
								}
							}
							currentChild = (NodeCraftML_OneLevelByPass) generateNodeWithoutSeeds(floor + 1, "t_t", dimXProj, dimYProj, sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
							currentChild.restoreNode(file, newRecord);


							children.add(currentChild);
							childrenInstancesIndex.add(instanceIndexes);
							childrenInstancesValues.add(instanceValues);
						}
					}
				}
			}
		}

	}

	public GenericNodeCraftML generateNodeWithSeeds(int floor, String id, int dimXProj,int dimYProj,int seedIndexX,int seedSignX,int seedIndexY,int seedSignY,int sparsity,String mode,int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		return new NodeCraftML_OneLevelByPass(floor, id,  dimXProj, dimYProj, seedIndexX, seedSignX, seedIndexY, seedSignY, sparsity, mode, branchFactor, sizeReservoir,depthMax,minInst,otherProjection);
	}

	public GenericNodeCraftML generateNodeWithoutSeeds(int floor, String id, int dimXProj,int dimYProj,int sparsity,String mode, int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		return new NodeCraftML_OneLevelByPass(floor, id, dimXProj, dimYProj, sparsity, mode, branchFactor, sizeReservoir, depthMax, minInst, otherProjection);
	}


	public void storeNode(RecordTextWriter file,float[] instanceValues,int[] instanceIndexes) {

		String[] recordNode = new String[12];
		recordNode[0] = Integer.toString(floor);
		recordNode[1] = id;
		if(floor == 0) {
			recordNode[2] = "none";
			recordNode[6] = "none";
		}else {
			recordNode[2] = id.substring(0, id.lastIndexOf('_'));

			if(instanceValues == null || instanceValues.length == 0){
				recordNode[6] = "none";
			}else{
				recordNode[6] = Integer.toString(instanceIndexes[0])+"="+Float.toString(instanceValues[0]);
				for(int i=1;i<instanceValues.length;i++){
					recordNode[6] = recordNode[6] + ";" + Integer.toString(instanceIndexes[i])+"="+Float.toString(instanceValues[i]);
				}
			}
		}

		if(isLeaf) {
			recordNode[3] = "leaf";
			recordNode[4] = "0";
			recordNode[5] = labelsPrediction;
		}else {
			recordNode[3] = "node";
			recordNode[4] = Integer.toString(children.size());
			recordNode[5] = "none";
		}

		recordNode[7] = Integer.toString(nbInstance);

		recordNode[8] = Integer.toString(seedIndexX);
		recordNode[9] = Integer.toString(seedSignX);
		recordNode[10] = Integer.toString(seedIndexY);
		recordNode[11] = Integer.toString(seedSignY);

		file.writeRecord(recordNode);

		if(!isLeaf) {
			for(int i = 0;i<children.size();i++) {
				children.get(i).storeNode(file, childrenInstancesValues.get(i),childrenInstancesIndex.get(i));
			}
		}

 	}


	public void learn(SmallItem x, SmallItem y){
		if(hasLearnt){
			if(isLeaf()){

			}else{
				int indexChildren = whichChild(projectVector(x, seedIndexX, seedSignX));
				children.get(indexChildren).learn(x, y);
			}
		}else{
			checkSame(x,y);
			if(reservoirKeyX.size()<sizeReservoirKmeans){
				addToReservoir(x, y,null);
			}else{
				int index = ThreadLocalRandom.current().nextInt(0, nbInstance + 1);
				if(index < sizeReservoirKmeans){
					addToReservoir(x, y,index);
				}
			}
			nbInstance++;
		}
	}


	public boolean indicationFinDePasse(){
		boolean killInstance = false;
		if(hasLearnt){
			if(isLeaf){

			}else{
				int i = 0;
				while (i<children.size()){
					if(children.get(i).indicationFinDePasse()){
						childrenInstancesValues.remove(i);
						childrenInstancesIndex.remove(i);
						children.remove(i);
					}else{
						i++;
					}
				}
			}
		}else{

			// checking if it will become a leaf or not
			if(nbInstance == 0){
				killInstance = true;
			}else{
				isLeaf = becomesLeaf();

				//TODO : dans CRAFTML il y avait incident si un noeud avait un unique enfant 5 fois de suite
				/*
				if(compteurIncident == 5) {
					isLeaf= true;
				}*/


				if(!isLeaf()){
					computesSeparativeInstances();
					children = new ArrayList<NodeCraftML_OneLevelByPass>();
					for(int i=0;i<childrenInstancesValues.size();i++){

						children.add((NodeCraftML_OneLevelByPass) generateNodeWithSeeds(floor+1, id + "_" + Integer.toString(i), dimXProj,dimYProj,seedIndexX,seedSignX,seedIndexY,seedSignY,sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection));

					}
				}else{
					SmallItem labelsPredictionLoc = new SmallItem();
					for(int i = 0;i<reservoirValuesY.size();i++){
						String[] currentKeyY = reservoirKeyY.get(i);
						float[] currentValuesY = reservoirValuesY.get(i);
						if(currentKeyY != null){
							for(int j = 0;j <currentKeyY.length;j++){
								float newValue = (labelsPredictionLoc.getValue(currentKeyY[j]) + currentValuesY[j]);
								labelsPredictionLoc.putKeyValue(currentKeyY[j],newValue);
							}
						}
					}
					for(String key:labelsPredictionLoc.getKeySet()){
						labelsPredictionLoc.putKeyValue(key,labelsPredictionLoc.getValue(key)/reservoirValuesY.size());
					}
					labelsPrediction = labelsPredictionLoc.getLinetext(";", "=");
				}

				hasLearnt = true;
				xCheck = null;
				yCheck = null;
				reservoirKeyX = null;
				reservoirKeyY = null;
				reservoirValuesX = null;
				reservoirValuesY = null;

			}


		}
		return killInstance;
	}

	public boolean isTrained() {
		if(hasLearnt){
			boolean isTrained = true;
			if(isLeaf()){
				return true;
			}else{
				for(int i=0;i<childrenInstancesValues.size();i++){
					if(!children.get(i).isTrained()){
						isTrained = false;
					}
				}
				return isTrained;
			}
		}else{
			return false;
		}
	}


	public void addToReservoir(SmallItem x, SmallItem y,Integer index){

		int i;
		if (x.getSize() == 0) {
			if(index == null){
				reservoirValuesX.add(null);
				reservoirKeyX.add(null);
			}else{
				reservoirValuesX.set(index, null);
				reservoirKeyX.set(index, null);
			}
		} else {
			float[] currValX = new float[x.getSize()];
			String[] currKeyX = new String[x.getSize()];
			i = 0;
			for (String key : x.getKeySet()) {
				currKeyX[i] = key;
				currValX[i] = x.getValue(key);
				i++;
			}

			if(index == null){
				reservoirValuesX.add(currValX);
				reservoirKeyX.add(currKeyX);
			}else{
				reservoirValuesX.set(index,currValX);
				reservoirKeyX.set(index,currKeyX);
			}
		}

		if (y.getSize() == 0) {

			if(index == null){
				reservoirValuesY.add(null);
				reservoirKeyY.add(null);
			}else{
				reservoirValuesY.set(index,null);
				reservoirKeyY.set(index,null);
			}

		} else {
			float[] currValY = new float[y.getSize()];
			String[] currKeyY = new String[y.getSize()];
			i = 0;
			for (String key : y.getKeySet()) {
				currKeyY[i] = key;
				currValY[i] = y.getValue(key);
				i++;
			}


			if(index == null){
				reservoirValuesY.add(currValY);
				reservoirKeyY.add(currKeyY);
			}else{
				reservoirValuesY.set(index,currValY);
				reservoirKeyY.set(index,currKeyY);
			}

		}
	}



	public SmallItem predict(float[] x) {
		if(isLeaf()){
			SmallItem labelsPredictionLoc = new SmallItem();
			labelsPredictionLoc.initViaLineIndexValue(labelsPrediction, ";", "=");
			return labelsPredictionLoc;
		}else{
			int index = whichChild(x);
			return children.get(index).predict(x);
		}
	}


	public SmallItem predict(SmallItem x) {
		if(isLeaf()){
			SmallItem labelsPredictionLoc = new SmallItem();
			labelsPredictionLoc.initViaLineIndexValue(labelsPrediction, ";", "=");
			return labelsPredictionLoc;
		}else{
			if(mode.equals("SXDY") || mode.equals("SXSY")) {
				float[] projX = projectVector(x, seedIndexX, seedSignX);
				int index = whichChild(projX);
				return children.get(index).predict(projX);
			}else {
				int index = whichChild(projectVector(x, seedIndexX, seedSignX));
				return children.get(index).predict(x);
			}
		}
	}


	public GenericNodeCraftML getLeaf(SmallItem x) {
		if(isLeaf()){
			return this;
		}else{
			if(mode.equals("SXDY") || mode.equals("SXSY")) {
				float[] projX = projectVector(x, seedIndexX, seedSignX);
				int index = whichChild(projX);
				return children.get(index).getLeaf(projX);
			}else {
				int index = whichChild(projectVector(x, seedIndexX, seedSignX));
				return children.get(index).getLeaf(x);
			}
		}
	}



	public GenericNodeCraftML getLeaf(float[] x){
		if(isLeaf()){
			return this;
		}else{
			int index = whichChild(x);
			return children.get(index).getLeaf(x);
		}
	}


	public void addPath(SmallItem x, ArrayList<GenericNodeCraftML> nodes) {
		if(isLeaf()){
			nodes.add(this);
		}else{
			nodes.add(this);
			if(mode.equals("SXDY") || mode.equals("SXSY")) {
				float[] projX = projectVector(x, seedIndexX, seedSignX);
				int index = whichChild(projX);
				children.get(index).addPath(projX,nodes);
			}else {
				int index = whichChild(projectVector(x, seedIndexX, seedSignX));
				children.get(index).addPath(x,nodes);
			}
		}
	}



	public void addPath(float[] x, ArrayList<GenericNodeCraftML> nodes){
		if(isLeaf()){
			nodes.add(this);
		}else{
			nodes.add(this);
			int index = whichChild(x);
			children.get(index).addPath(x,nodes);
		}
	}


	public void addNativeInformation(NativeInformation nativeInformation){

		if(nodeNativeInformation == null){
			nodeNativeInformation = new ArrayList<NativeInformation>();
			nodeNativeInformation.add(nativeInformation);
		}else if (nodeNativeInformation.size() < sizeReservoirNativeInformation){
			nodeNativeInformation.add(nativeInformation);
		}else{
			// mode reservoir
			int index = ThreadLocalRandom.current().nextInt(0, nbNativeInformation + 1);
			if(index < sizeReservoirNativeInformation){
				nodeNativeInformation.add(nativeInformation);
			}
		}

		nbNativeInformation++;
	}

	public ArrayList<NativeInformation> getNativeInformation(){
		return nodeNativeInformation;
	}



	boolean isLeaf(){
		return isLeaf;
	}



	public void showStats(){
		if(isLeaf()){
			if(nbInstance > 0){
				messageTrace(id + "\t" + nbInstance);
			}
		}else{
			for(int i=0;i<children.size();i++){
				children.get(i).showStats();
			}
		}
	}



	public void error(String mess) {
		messageTrace("Error:\n" + mess);
		Error er = new Error(mess);
		throw er;
	}



	/**
	 * Computes the centroids (case not projected)
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 */
	public void computesSeparativeInstances(){


		ArrayList<float[]> reservoirValues = new ArrayList<float[]>();
		ArrayList<int[]> reservoirIndexes = new ArrayList<int[]>();

		int currentIndex;
		float currentSign;

		float normalization = 0;

		for(int i = 0;i<reservoirKeyY.size();i++){

			/*
			for(String key:current.getKeySet()){
				for(int j = 0;j<dimYProj;j++){
					reservoirArray[i][j] = reservoirArray[i][j] + getWeight(key + "_" + Integer.toString(j), seedIndex)*current.getValue(key);
				}
			}

			normalization = (float) Math.sqrt(VectorOperators.dotProduct(reservoirArray[i], reservoirArray[i]));

			for(int j = 0;j<dimYProj;j++){
				reservoirArray[i][j] = reservoirArray[i][j]/normalization;
			}
			*/

			String[] currentKeyY = reservoirKeyY.get(i);
			float[] currentValuesY = reservoirValuesY.get(i);

			TreeMap<Integer, Float> currentProjectedInstance = new TreeMap<Integer,Float>();

			if(currentKeyY != null){
				for(int j = 0;j <currentKeyY.length;j++){

					currentIndex = getIndex(currentKeyY[j], seedIndexY, dimYProj);
					currentSign = getSign(currentKeyY[j], seedSignY);
					if(currentProjectedInstance.containsKey(currentIndex)){
						currentProjectedInstance.put(currentIndex, currentProjectedInstance.get(currentIndex) + currentSign*currentValuesY[j]);
					}else{
						currentProjectedInstance.put(currentIndex, currentSign*currentValuesY[j]);
					}

					/*
					for(int k = 0;k<dimYProj;k++){
						reservoirArray[i][j] = reservoirArray[i][j] + getWeight(currentKeyY[j]+ "_" + Integer.toString(k), seedIndex);
					}*/
				}
			}


			normalization = 0;
			int nonzeros = 0;
			for(Integer currInd:currentProjectedInstance.keySet()){
				normalization = normalization + currentProjectedInstance.get(currInd)*currentProjectedInstance.get(currInd);
				if(currentProjectedInstance.get(currInd) != 0f){
					nonzeros++;
				}
			}

			normalization =  (float) Math.sqrt(normalization);



			float[] currentValues;
			int[] currentIndexes;

			//TODO to improve
			if(normalization == 0){
				//System.out.println("null norm vector...");
				currentValues = new float[1];
				currentIndexes = new int[1];
				currentValues[0] = 1f;
				currentIndexes[0] = ThreadLocalRandom.current().nextInt(0, dimYProj);
			}else{
				currentValues = new float[nonzeros];
				currentIndexes = new int[nonzeros];
				int k = 0;
				for(Integer currInd:currentProjectedInstance.keySet()){
					if(currentProjectedInstance.get(currInd) != 0f){
						currentIndexes[k] = currInd;
						currentValues[k] = currentProjectedInstance.get(currInd)/normalization;
						k++;
					}
				}
			}

			reservoirValues.add(currentValues);
			reservoirIndexes.add(currentIndexes);

		}

		KmeansCosineSparse km = new KmeansCosineSparse(branchFactor, reservoirValues,reservoirIndexes, 5,dimYProj);

		km.computeClusters();

		//km.cleanClusters();

		float[][] clusters = km.getClusters();

		int[] indexInstanceCluster = km.getIndexClusters();

		km = null;

		int indexCurrentClust;
		float[] currentX;

		float[][] childrenInstancesCurrent = new float[branchFactor][dimXProj];

		childrenInstancesValues = new ArrayList<float[]>();
		childrenInstancesIndex = new ArrayList<int[]>();


		int[] nbInst = new int[branchFactor];

		for(int i=0;i<reservoirKeyX.size();i++){
			indexCurrentClust = indexInstanceCluster[i];
			currentX = projectVector(reservoirKeyX.get(i),reservoirValuesX.get(i), seedIndexX, seedSignX);
			nbInst[indexCurrentClust]++;
			for(int j = 0;j<dimXProj;j++){
				childrenInstancesCurrent[indexCurrentClust][j] = childrenInstancesCurrent[indexCurrentClust][j] + currentX[j];
			}

		}

		for(int i=0;i<branchFactor;i++){
			if(nbInst[i]!=0){
				/*
				float norm = (float) Math.sqrt(VectorOperators.dotProduct(childrenInstancesCurrent[i],childrenInstancesCurrent[i]));
				for(int j = 0;j<dimXProj;j++){
					childrenInstancesCurrent[i][j] = childrenInstancesCurrent[i][j]/norm;
				}*/
				addInstance(childrenInstancesCurrent[i]);
			}else{
				//System.out.println("zero !!");
			}
		}

	}


	/**
	 * Sparsify a centroid and adds it to the list of centroids (children)
	 * @param fullInstance
	 */
	void addInstance(float[] fullInstance) {
		int[] bestIndexes = new int[sparsity];
		float[] bestValues = new float[sparsity];

		int indexMin;
		float valueMin;

		for(int i = 0;i<sparsity;i++) {
			bestValues[i] = Math.abs(fullInstance[i]);
			bestIndexes[i] = i;
		}

		indexMin = smallestItemIndex(bestValues);
		valueMin = bestValues[indexMin];

		for(int i = sparsity;i<fullInstance.length;i++) {
			if(Math.abs(fullInstance[i]) > valueMin) {
				bestValues[indexMin] = Math.abs(fullInstance[i]);
				bestIndexes[indexMin] = i;
				indexMin = smallestItemIndex(bestValues);
				valueMin = bestValues[indexMin];
			}
		}

		for(int i = 0;i<sparsity;i++) {
			 bestValues[i] = fullInstance[bestIndexes[i]];
		}

		float normvec = 0f;
		int nonzeros = 0;

		for(int i = 0;i<sparsity;i++) {
			if(bestValues[i] != 0f){
				nonzeros++;
				normvec = normvec + bestValues[i]*bestValues[i];
			}
		}

		normvec = (float) Math.sqrt(normvec);

		int[] bestIndexesSparse = new int[nonzeros];
		float[] bestValuesSparse = new float[nonzeros];

		int j = 0;
		for(int i = 0;i<sparsity;i++) {
			if(bestValues[i] != 0f){
				bestIndexesSparse[j] = bestIndexes[i];
				bestValuesSparse[j] = bestValues[i]/normvec;
				j++;
			}
		}

		childrenInstancesIndex.add(bestIndexesSparse);
		childrenInstancesValues.add(bestValuesSparse);
	}


	public int smallestItemIndex(float[] array){
		float minVal = array[0];
		int minIdx = 0;
		for(int idx=1; idx<array.length; idx++) {
			if(array[idx] < minVal) {
				minVal = array[idx];
				minIdx = idx;
			}
		}
		return minIdx;
	}


	/*
	int getIndex(String key,int seed,int sizeMax){
		String hashKey = Integer.toString(seed) + key;
		return Math.abs(FNV1a.hash32(12)  % sizeMax );
	}

	float getSign(String key,int seed){
		String hashKey = Integer.toString(seed) + key;
		return Math.abs(FNV1a.hash32(12) % 2)*2f - 1f;
	}*/



	int getIndex(String key,int seed,int sizeMax){
		String hashKey = "azv" + key;
		return Math.abs(Murmur2.hash32(hashKey, seed) % sizeMax);
	}

	float getSign(String key,int seed){
		String hashKey = "azv" + key;
		if(otherProjection){
			return Math.abs(Murmur2.hash32(hashKey, seed) % 3) - 1f;
		}else {
			return Math.abs(Murmur2.hash32(hashKey, seed) % 2)*2f - 1f;
		}
	}


	/*
	int getIndex(String key,int seed,int sizeMax){
		String hashKey = "blablubla" + key;
		return Math.abs(hashKey.hashCode() % sizeMax);
	}

	float getSign(String key,int seed){
		String hashKey = "blablubla" + key;
		return Math.abs(hashKey.hashCode() % 2)*2f - 1f;
	}*/



	public boolean becomesLeaf(){
		return (floor >= depthMax) || (nbInstance < minInst) || checkSameX || checkSameY;
	}



	public void checkSame(SmallItem x, SmallItem y) {

		if(checkSameX){
			if(nbInstance == 0){
				xCheck = x.getClone();
			}else{
				if(!sameSmallItem(xCheck, x)){
					checkSameX = false;
				}
			}
		}
		if(checkSameY){
			if(nbInstance == 0){
				yCheck = y.getClone();
			}else{
				if(!sameSmallItem(yCheck, y)){
					checkSameY = false;
				}
			}
		}

	}


	/**
	 * Check if the two smallItem are indentical
	 * @param y1
	 * @param y2
	 * @return
	 */
	public boolean sameSmallItem(SmallItem y1,SmallItem y2){

		String[] y1keyArray = y1.getKeyArray();
		String[] y2keyArray = y2.getKeyArray();

		if(y1keyArray == null && y2keyArray != null){
			return false;
		}else if(y1keyArray != null && y2keyArray == null){
			return false;
		}else if(y1keyArray == null && y2keyArray == null){
			return true;
		}else{
			boolean out = true;
			if(y1keyArray.length == y2keyArray.length){
				for(String key:y1.getKeySet()){
					if(y1.getValue(key) != y2.getValue(key)){
						out = false;
					}
				}
			}else{
				out = false;
			}
			return out;
		}

	}

	/**
	 * Computes a sparse dot product between the input x and the centroid of index indexInstance
	 * @param x
	 * @param indexInstance
	 * @return
	 */
	float sparseDotProduct(float[] x,int indexInstance) {
		float[] values = childrenInstancesValues.get(indexInstance);
		int[] indexes = childrenInstancesIndex.get(indexInstance);
		float out = 0;
		for(int i = 0;i<values.length;i++) {
			out = out + values[i]*x[indexes[i]];
		}
		return out;
	}


	/**
	 * Returns the index of the child that x will be directed to
	 * @param x
	 * @return
	 */
	int whichChild(float[] x){

		float currentMin = 1f-sparseDotProduct(x, 0);

		int currentMinIndex = 0;
		float newDistance;
		for(int i = 1;i<childrenInstancesIndex.size();i++){
			newDistance = 1f-sparseDotProduct(x, i);
			if(newDistance < currentMin){
				currentMin = newDistance;
				currentMinIndex = i;
			}
		}
		return currentMinIndex;
	}



	/**
	 * Projects a sparse vector (xKey, xValues) given seeds and returns a dense vector
	 * @param x
	 * @param seedIndex
	 * @param seedSign
	 * @return
	 */
	float[] projectVector(String[] xKey,float[] xValues, int seedIndex,int seedSign) {

		float[] projectedX = new float[dimXProj];


		int currentIndex;

		float currentSign;


		//TODO mieux gerer le cas ou x est null ?

		if(xKey!=null) {

		for(int i = 0;i<xKey.length;i++){
			currentIndex = getIndex(xKey[i], seedIndex, dimXProj);
			currentSign = getSign(xKey[i], seedSign);
			projectedX[currentIndex] = projectedX[currentIndex] + currentSign * xValues[i];
		}

		float norm = 0;
		for(int i = 0;i<projectedX.length;i++){
			norm += projectedX[i]*projectedX[i];
		}

		norm = (float) Math.sqrt(norm);

		for(int i = 0;i<projectedX.length;i++){
			projectedX[i] /= norm;
		}

		}

		return projectedX;
	}



	/**
	 * Projects a smallitem x given seeds and returns a dense vector
	 * @param x
	 * @param seedIndex
	 * @param seedSign
	 * @return
	 */
	float[] projectVector(SmallItem x, int seedIndex,int seedSign) {

		float[] projectedX = new float[dimXProj];

		int currentIndex;

		float currentSign;

		for (String key : x.getKeySet()) {
			currentIndex = getIndex(key, seedIndex, dimXProj);
			currentSign = getSign(key, seedSign);
			projectedX[currentIndex] = projectedX[currentIndex] + currentSign * x.getValue(key);
		}

		float norm = 0;
		for(int i = 0;i<projectedX.length;i++){
			norm += projectedX[i]*projectedX[i];
		}

		norm = (float) Math.sqrt(norm);

		for(int i = 0;i<projectedX.length;i++){
			projectedX[i] /= norm;
		}

		return projectedX;

	}



	public int getNbInstanceLeaves(){
		if(isTrained()){
			if(isLeaf()){
				return nbInstance;
			}else {
				int total = 0;
				for(int i = 0; i < childrenInstancesValues.size();i++){
					total = total + children.get(i).getNbInstanceLeaves();
				}
				return total;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}
	}


	public int getMaxInstanceLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return nbInstance;
			}else {
				int maxInst = children.get(0).getMaxInstanceLeaf();
				for(int i = 1; i < childrenInstancesValues.size();i++){
					int currInst = children.get(i).getMaxInstanceLeaf();
					if(currInst > maxInst){
						maxInst = currInst;
					}
				}
				return maxInst;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}


	}

	public int getMinInstanceLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return nbInstance;
			}else {
				int minInst = children.get(0).getMinInstanceLeaf();
				for(int i = 1; i < childrenInstancesValues.size();i++){
					int currInst = children.get(i).getMinInstanceLeaf();
					if(currInst < minInst){
						minInst = currInst;
					}
				}
				return minInst;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}


	}


	public int getNbLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return 1;
			}else {
				int total = 0;
				for(int i = 0; i < childrenInstancesValues.size();i++){
					total = total + children.get(i).getNbLeaf();
				}
				return total;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}


	}

	public int getNbNode(){
		if(isTrained()){
			if(isLeaf()){
				return 0;
			}else {
				int total = 0;
				for(int i = 0; i < childrenInstancesValues.size();i++){
					total = total + children.get(i).getNbNode();
				}
				return 1 + total;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}


	}

	public int sumDepthLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return floor;
			}else {
				int total = 0;
				for(int i = 0; i < childrenInstancesValues.size();i++){
					total = total + children.get(i).sumDepthLeaf();
				}
				return total;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}

	}

	public int getMaxDepthLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return floor;
			}else {
				int maxFloor = children.get(0).getMaxDepthLeaf();
				for(int i = 1; i < childrenInstancesValues.size();i++){
					int currFloor = children.get(i).getMaxDepthLeaf();
					if(currFloor > maxFloor){
						maxFloor = currFloor;
					}
				}
				return maxFloor;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}

	}


	public int getMinDepthLeaf(){
		if(isTrained()){
			if(isLeaf()){
				return floor;
			}else {
				int minFloor = children.get(0).getMinDepthLeaf();
				for(int i = 1; i < childrenInstancesValues.size();i++){
					int currFloor = children.get(i).getMinDepthLeaf();
					if(currFloor < minFloor){
						minFloor = currFloor;
					}
				}
				return minFloor;
			}
		}else{
			error("Cannot audit before/during training");
			return 0;
		}

	}



	@Override
	public void learn(ArrayList<String[]> keyX, ArrayList<float[]> valuesX, ArrayList<Integer> useInst,
			ArrayList<String[]> keyY, ArrayList<float[]> valuesY, int compteurIncident) {

		error("cannot call this function on this mode of CRAFTML");

	}


	@Override
	public GenericNodeCraftML[] getChildren() {

		GenericNodeCraftML[] out = new GenericNodeCraftML[children.size()];
		for (int i = 0; i < children.size();i++){
			out[i] = children.get(i);
		}

		return out;
	}



}

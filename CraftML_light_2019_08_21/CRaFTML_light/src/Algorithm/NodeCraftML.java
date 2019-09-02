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
public class NodeCraftML implements GenericNodeCraftML {


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

	ArrayList<NodeCraftML> children;

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



	float[] xCheck;
	SmallItem yCheck;

	int sparsity;

	String mode;

	ArrayList<NativeInformation> nodeNativeInformation = null;

	int nbNativeInformation = 0;

	public String getID(){
		return id;
	}

	public void messageTrace(String message) {
		Displayer.displayText(message);
	}


	public NodeCraftML(int floor, String id, int dimXProj,int dimYProj,int seedIndexX,int seedSignX,int seedIndexY,int seedSignY,int sparsity,String mode,int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
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


		this.otherProjection = otherProjection;

		nbInstance = 0;

	}



	public NodeCraftML(int floor, String id, int dimXProj,int dimYProj,int sparsity,String mode, int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
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


		this.otherProjection = otherProjection;

		nbInstance = 0;

	}


	public void restoreNode(RecordTextReader file,String[] record) {


		if(floor != Integer.parseInt(record[0])) {
			error("incoherence étage");
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

			children = new ArrayList<NodeCraftML>();
			childrenInstancesValues = new ArrayList<float[]>();
			childrenInstancesIndex = new ArrayList<int[]>();

			int realNbChildren = Integer.parseInt(record[4]);

			String[] newRecord;
			float[] instanceValues;
			int[] instanceIndexes;

			GenericNodeCraftML currentChild;

			for(int i = 0;i<realNbChildren;i++) {

				newRecord = file.readPureRecord();

				if(newRecord.length!=12) {
					error("enregistrement de la mauvaise taille");
				}else {

					if(newRecord[6].equals("none")){
						//TODO ce cas n'arrive jamais car les centroids séparateur ne sont jamais nuls
						instanceValues = new float[0];
						instanceIndexes = new int[0];
						currentChild = generateNodeWithoutSeeds(floor + 1, "t_t", dimXProj, dimYProj, sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
						currentChild.restoreNode(file, newRecord);
						children.add((NodeCraftML) currentChild);
						childrenInstancesIndex.add(instanceIndexes);
						childrenInstancesValues.add(instanceValues);
					}else{
						String[] instanceString = newRecord[6].split(";");
						instanceValues = new float[instanceString.length];
						instanceIndexes = new int[instanceString.length];
						if(instanceString.length > dimXProj) {
							error("incohérence dans la taille du séparateur");
						}else {
							for(int j = 0;j<instanceString.length;j++) {
								String[] spl = instanceString[j].split("=");
								if(spl.length == 2){
									int currIndex = Integer.parseInt(spl[0]);
									if(currIndex > dimXProj){
										error("incohérence dans la taille du séparateur");
									}else{
										instanceIndexes[j] = currIndex;
										instanceValues[j] = Float.parseFloat(spl[1]);
									}
								}else{
									error("problème dans le format du séparateur");
								}
							}
							currentChild = generateNodeWithoutSeeds(floor + 1, "t_t", dimXProj, dimYProj, sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection);
							currentChild.restoreNode(file, newRecord);


							children.add((NodeCraftML) currentChild);
							childrenInstancesIndex.add(instanceIndexes);
							childrenInstancesValues.add(instanceValues);
						}
					}
				}
			}
		}

	}

	public GenericNodeCraftML generateNodeWithSeeds(int floor, String id, int dimXProj,int dimYProj,int seedIndexX,int seedSignX,int seedIndexY,int seedSignY,int sparsity,String mode,int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		return new NodeCraftML(floor, id,  dimXProj, dimYProj, seedIndexX, seedSignX, seedIndexY, seedSignY, sparsity, mode, branchFactor, sizeReservoir,depthMax,minInst,otherProjection);
	}

	public GenericNodeCraftML generateNodeWithoutSeeds(int floor, String id, int dimXProj,int dimYProj,int sparsity,String mode, int branchFactor, int sizeReservoir, int depthMax,int minInst,boolean otherProjection) {
		return new NodeCraftML(floor, id, dimXProj, dimYProj, sparsity, mode, branchFactor, sizeReservoir, depthMax, minInst, otherProjection);
	}


	public void storeNode(RecordTextWriter file,float[] instanceValues,int[] instanceIndexes) {

		/* champs 0 = niveau
		 * champs 1 = id
		 * champs 2 = id parent
		 * champs 3 = leaf or node
		 * champs 4 = nombre d'enfants
		 * champs 5 = vecteur de label dans les feuilles (none si noeud) sep ; et =
		 * champs 6 = centroïd qui amene à ce noeud. sep ; et =
		 * champs 7 = nombre d'instances
		 * champs 8 à 11 = graines (2 graines pour x : une pour l'index et une pour le signe, idem pour y)
		*/

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


	/**
	 * Depending on the mode, the learning function is different for acceleration purposes
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 * @param compteurIncident
	 */
	public void learn(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY,int compteurIncident) {

		if(mode.equals("SXSY")){

			learnSX(keyX, valuesX, useInst, keyY, valuesY, compteurIncident);

		}else if(mode.equals("DXDY")){

			learnDX(keyX, valuesX, useInst, keyY, valuesY, compteurIncident);

		}else if(mode.equals("SXDY")){

			learnSX(keyX, valuesX, useInst, keyY, valuesY, compteurIncident);

		}else if(mode.equals("DXSY")){

			learnDX(keyX, valuesX, useInst, keyY, valuesY, compteurIncident);

		}else{
			error("Mode invalide");
		}

	}



	/**
	 * Here the instances are projected in each node
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 * @param compteurIncident
	 */
	public void learnDX(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY,int compteurIncident) {

		nbInstance = useInst.size();

		checkSameX = true;
		checkSameY = true;


		if(useInst.size()<2000){
			checkSame(keyX,valuesX,useInst,keyY,valuesY);
		}else{
			checkSameX = false;
			checkSameY = false;
		}

		isLeaf = becomesLeaf();

		if(compteurIncident == 5) {
			isLeaf= true;
		}

		if(floor == depthMax){
			isLeaf=true;
		}


		if(isLeaf()){

			SmallItem labelsPredictionLoc = new SmallItem();
			for(int i = 0; i<useInst.size();i++){
				String[] currentKeyY = keyY.get(useInst.get(i));
				float[] currentValuesY = valuesY.get(useInst.get(i));
				if(currentKeyY != null){
					for(int j = 0;j <currentKeyY.length;j++){
						float newValue = (labelsPredictionLoc.getValue(currentKeyY[j]) + currentValuesY[j]);
						labelsPredictionLoc.putKeyValue(currentKeyY[j],newValue);
					}
				}
			}

			for(String key:labelsPredictionLoc.getKeySet()){
				labelsPredictionLoc.putKeyValue(key,labelsPredictionLoc.getValue(key)/nbInstance);
			}

			labelsPrediction = labelsPredictionLoc.getLinetext(";", "=");

		}else{

			computesSeparativeInstances(keyX,valuesX,useInst,keyY,valuesY);

			children = new ArrayList<NodeCraftML>();

			ArrayList<ArrayList<Integer>> useInstChildren = new ArrayList<ArrayList<Integer>>();
			Random random = new Random();
			for(int i=0;i<childrenInstancesValues.size();i++){

				children.add((NodeCraftML) generateNodeWithSeeds(floor+1, id + "_" + Integer.toString(i), dimXProj,dimYProj,seedIndexX,seedSignX,seedIndexY,seedSignY,sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection));
				useInstChildren.add(new ArrayList<Integer>());
			}

			for(int i = 0; i<useInst.size();i++){
				useInstChildren.get(whichChild(projectVector(keyX.get(useInst.get(i)),valuesX.get(useInst.get(i)), seedIndexX, seedSignX))).add(useInst.get(i));
			}

			int k = 0;
			while(k < childrenInstancesValues.size()){
				if(useInstChildren.get(k).size() == 0){
					childrenInstancesValues.remove(k);
					childrenInstancesIndex.remove(k);
					children.remove(k);
					useInstChildren.remove(k);
				}else{
					k++;
				}
			}

			int newCompteurIncident = 0;

			if(childrenInstancesValues.size() == 1) {
				newCompteurIncident = compteurIncident + 1;
			}

			for(int i=0;i<childrenInstancesValues.size();i++){
				children.get(i).learnDX(keyX,valuesX, useInstChildren.get(i), keyY, valuesY,newCompteurIncident);
				useInstChildren.set(i, null);
			}

		}

	}

	/**
	 * Here the instances are projected in the first node only
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 * @param compteurIncident
	 */
	public void learnSX(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY,int compteurIncident) {

		nbInstance = useInst.size();

		checkSameX = true;
		checkSameY = true;


		Random random = new Random();


		ArrayList<int[]> indexesX = new ArrayList<int[]>();
		ArrayList<float[]> newValuesX =  new ArrayList<float[]>();

		String[] xKey;
		float[] xValues;
		int currentIndex;
		float currentSign;
		float[] projectedX;

		// projection de tous les attributs et normalisation
		for(int j = 0; j<useInst.size();j++){
			xKey = keyX.get(useInst.get(j));
			xValues = valuesX.get(useInst.get(j));

			if(xKey!=null) {

				projectedX = new float[dimXProj];

				for(int i = 0;i<xKey.length;i++){
					currentIndex = getIndex(xKey[i], seedIndexX, dimXProj);
					currentSign = getSign(xKey[i], seedSignX);
					projectedX[currentIndex] = projectedX[currentIndex] + currentSign * xValues[i];
				}

				float norm = 0;
				int nonZeros = 0;

				for(int i = 0;i<projectedX.length;i++){
					if(projectedX[i] != 0f) {
						norm += projectedX[i]*projectedX[i];
						nonZeros++;
					}
				}

				if(nonZeros == 0) {
					indexesX.add(null);
					newValuesX.add(null);
				}else {
					norm = (float) Math.sqrt(norm);
					int[] currIndexes = new int[nonZeros];
					float[] currValues = new float[nonZeros];

					int k = 0;

					for(int i = 0;i<projectedX.length;i++){
						if(projectedX[i] != 0f) {
							currIndexes[k] = i;
							currValues[k] = projectedX[i]/norm;
							k++;
						}
					}

					indexesX.add(currIndexes);
					newValuesX.add(currValues);
				}

			}else {
				indexesX.add(null);
				newValuesX.add(null);
			}
		}



		// TODO Probleme si plus de 2000 clones
		if(useInst.size()<2000){
			checkSameInt(indexesX,newValuesX,useInst,keyY,valuesY);
		}else{
			checkSameX = false;
			checkSameY = false;
		}

		isLeaf = becomesLeaf();

		if(compteurIncident == 5) {
			isLeaf= true;
		}

		if(floor == depthMax){
			isLeaf=true;
		}


		if(isLeaf()){

			SmallItem labelsPredictionLoc = new SmallItem();
			for(int i = 0; i<useInst.size();i++){
				String[] currentKeyY = keyY.get(useInst.get(i));
				float[] currentValuesY = valuesY.get(useInst.get(i));
				if(currentKeyY != null){
					for(int j = 0;j <currentKeyY.length;j++){
						float newValue = (labelsPredictionLoc.getValue(currentKeyY[j]) + currentValuesY[j]);
						labelsPredictionLoc.putKeyValue(currentKeyY[j],newValue);
					}
				}
			}

			for(String key:labelsPredictionLoc.getKeySet()){
				labelsPredictionLoc.putKeyValue(key,labelsPredictionLoc.getValue(key)/nbInstance);
			}

			labelsPrediction = labelsPredictionLoc.getLinetext(";", "=");

		}else{

			computesSeparativeInstancesInt(indexesX,newValuesX,useInst,keyY,valuesY);

			children = new ArrayList<NodeCraftML>();

			ArrayList<ArrayList<Integer>> useInstChildren = new ArrayList<ArrayList<Integer>>();
			for(int i=0;i<childrenInstancesValues.size();i++){

				children.add((NodeCraftML) generateNodeWithSeeds(floor+1, id + "_" + Integer.toString(i), dimXProj,dimYProj,seedIndexX,seedSignX,seedIndexY,seedSignY,sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection));
				useInstChildren.add(new ArrayList<Integer>());
			}

			for(int i = 0; i<useInst.size();i++){
				useInstChildren.get(whichChild(restoreVector(indexesX.get(useInst.get(i)),newValuesX.get(useInst.get(i))))).add(useInst.get(i));
			}

			int k = 0;
			while(k < childrenInstancesValues.size()){
				if(useInstChildren.get(k).size() == 0){
					childrenInstancesValues.remove(k);
					childrenInstancesIndex.remove(k);
					children.remove(k);
					useInstChildren.remove(k);
				}else{
					k++;
				}
			}

			int newCompteurIncident = 0;

			if(childrenInstancesValues.size() == 1) {
				newCompteurIncident = compteurIncident + 1;
			}

			for(int i=0;i<childrenInstancesValues.size();i++){
				children.get(i).learnChildSX(indexesX,newValuesX, useInstChildren.get(i), keyY, valuesY,newCompteurIncident);
				useInstChildren.set(i, null);
			}

		}

		indexesX = null;
		newValuesX = null;

	}



	public void learnChildSX(ArrayList<int[]> indexesX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY,int compteurIncident) {

		nbInstance = useInst.size();

		checkSameX = true;
		checkSameY = true;


		if(useInst.size()<2000){
			checkSameInt(indexesX,valuesX,useInst,keyY,valuesY);
		}else{
			checkSameX = false;
			checkSameY = false;
		}

		isLeaf = becomesLeaf();

		if(compteurIncident == 5) {
			isLeaf= true;
		}

		if(floor == depthMax){
			isLeaf=true;
		}


		if(isLeaf()){

			SmallItem labelsPredictionLoc = new SmallItem();
			for(int i = 0; i<useInst.size();i++){
				String[] currentKeyY = keyY.get(useInst.get(i));
				float[] currentValuesY = valuesY.get(useInst.get(i));
				if(currentKeyY != null){
					for(int j = 0;j <currentKeyY.length;j++){
						float newValue = (labelsPredictionLoc.getValue(currentKeyY[j]) + currentValuesY[j]);
						labelsPredictionLoc.putKeyValue(currentKeyY[j],newValue);
					}
				}
			}

			for(String key:labelsPredictionLoc.getKeySet()){
				labelsPredictionLoc.putKeyValue(key,labelsPredictionLoc.getValue(key)/nbInstance);
			}

			labelsPrediction = labelsPredictionLoc.getLinetext(";", "=");

		}else{

			computesSeparativeInstancesInt(indexesX,valuesX,useInst,keyY,valuesY);

			children = new ArrayList<NodeCraftML>();

			ArrayList<ArrayList<Integer>> useInstChildren = new ArrayList<ArrayList<Integer>>();
			Random random = new Random();
			for(int i=0;i<childrenInstancesValues.size();i++){

				children.add((NodeCraftML) generateNodeWithSeeds(floor+1, id + "_" + Integer.toString(i), dimXProj,dimYProj,seedIndexX,seedSignX,seedIndexY,seedSignY,sparsity,mode,branchFactor,sizeReservoirKmeans,depthMax,minInst,otherProjection));
				useInstChildren.add(new ArrayList<Integer>());
			}

			for(int i = 0; i<useInst.size();i++){
				useInstChildren.get(whichChild(restoreVector(indexesX.get(useInst.get(i)),valuesX.get(useInst.get(i))))).add(useInst.get(i));
			}

			int k = 0;
			while(k < childrenInstancesValues.size()){
				if(useInstChildren.get(k).size() == 0){
					childrenInstancesValues.remove(k);
					childrenInstancesIndex.remove(k);
					children.remove(k);
					useInstChildren.remove(k);
				}else{
					k++;
				}
			}

			int newCompteurIncident = 0;

			if(childrenInstancesValues.size() == 1) {
				newCompteurIncident = compteurIncident + 1;
			}

			for(int i=0;i<childrenInstancesValues.size();i++){
				children.get(i).learnChildSX(indexesX,valuesX, useInstChildren.get(i), keyY, valuesY,newCompteurIncident);
				useInstChildren.set(i, null);
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


	public NodeCraftML getLeaf(SmallItem x) {
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



	public NodeCraftML getLeaf(float[] x){
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
	 * Computes the centroids (case already projected)
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 */
	public void computesSeparativeInstancesInt(ArrayList<int[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY){

		int reservoirSize = Math.min(useInst.size(), sizeReservoirKmeans);

		ArrayList<float[]> reservoirValues = new ArrayList<float[]>();
		ArrayList<int[]> reservoirIndexes = new ArrayList<int[]>();

		int[] reservoirIndex = new int[reservoirSize];

		//reservoir sampling
		if(useInst.size() < sizeReservoirKmeans){
			for(int i = 0;i<useInst.size();i++){
				reservoirIndex[i] = useInst.get(i);
			}
		}else{
			for(int i = 0;i<sizeReservoirKmeans;i++){
				reservoirIndex[i] = useInst.get(i);
			}
			for(int i = sizeReservoirKmeans;i<useInst.size();i++){
				int index = ThreadLocalRandom.current().nextInt(0, i + 1);
				if(index < sizeReservoirKmeans){
					reservoirIndex[index] = useInst.get(i);
				}
			}
		}


		int currentIndex;
		float currentSign;



		float normalization = 0;

		for(int i = 0;i<reservoirSize;i++){

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
			String[] currentKeyY = keyY.get(reservoirIndex[i]);
			float[] currentValuesY = valuesY.get(reservoirIndex[i]);

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
				//System.out.println("Vecteur avec norme nulle");
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

		//TODO 5 iterations kmeans en dur
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

		for(int i=0;i<reservoirSize;i++){
			indexCurrentClust = indexInstanceCluster[i];
			currentX = restoreVector(keyX.get(reservoirIndex[i]),valuesX.get(reservoirIndex[i]));
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
	 * Computes the centroids (case not projected)
	 * @param keyX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 */
	public void computesSeparativeInstances(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY){

		int reservoirSize = Math.min(useInst.size(), sizeReservoirKmeans);

		ArrayList<float[]> reservoirValues = new ArrayList<float[]>();
		ArrayList<int[]> reservoirIndexes = new ArrayList<int[]>();

		int[] reservoirIndex = new int[reservoirSize];

		if(useInst.size() < sizeReservoirKmeans){
			for(int i = 0;i<useInst.size();i++){
				reservoirIndex[i] = useInst.get(i);
			}
		}else{
			for(int i = 0;i<sizeReservoirKmeans;i++){
				reservoirIndex[i] = useInst.get(i);
			}
			for(int i = sizeReservoirKmeans;i<useInst.size();i++){
				int index = ThreadLocalRandom.current().nextInt(0, i + 1);
				if(index < sizeReservoirKmeans){
					reservoirIndex[index] = useInst.get(i);
				}
			}
		}


		int currentIndex;
		float currentSign;



		float normalization = 0;

		for(int i = 0;i<reservoirSize;i++){

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
			String[] currentKeyY = keyY.get(reservoirIndex[i]);
			float[] currentValuesY = valuesY.get(reservoirIndex[i]);

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
				//System.out.println("Vecteur avec norme nulle");
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

		for(int i=0;i<reservoirSize;i++){
			indexCurrentClust = indexInstanceCluster[i];
			currentX = projectVector(keyX.get(reservoirIndex[i]),valuesX.get(reservoirIndex[i]), seedIndexX, seedSignX);
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
		return (nbInstance < minInst) || checkSameX || checkSameY;
	}


	/**
	 * Check if all the nodes' instances are clones with respect to their features or labels
	 * @param indexesX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 */
	public void checkSame(ArrayList<String[]> keyX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY) {


		String[] xKeyCheck = keyX.get(useInst.get(0));
		float[] xValuesCheck = valuesX.get(useInst.get(0));

		int i = 1;
		while(checkSameX && i < useInst.size()){
			if(!sameVector(xKeyCheck,xValuesCheck, keyX.get(useInst.get(i)),valuesX.get(useInst.get(i)))){
				checkSameX = false;
			}
			i++;
		}


		String[] yKeyCheck = keyY.get(useInst.get(0));
		float[] yValuesCheck = valuesY.get(useInst.get(0));


		i = 1;
		while(!checkSameX && checkSameY && i < useInst.size()){
			if(!sameVector(yKeyCheck,yValuesCheck, keyY.get(useInst.get(i)),valuesY.get(useInst.get(i)))){
				checkSameY = false;
			}
			i++;
		}

	}

	public void checkSame(float[] x, SmallItem y) {

		if(checkSameX){
			if(nbInstance == 0){
				xCheck = x.clone();
			}else{
				if(!sameVector(xCheck, x)){
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
	 * Check if the two vectors are the same
	 * @param key1
	 * @param values1
	 * @param key2
	 * @param values2
	 * @return
	 */
	public boolean sameVector(float[] x1,float[] x2) {
		boolean out = true;
		int i = 0;
		if(x1.length == x2.length){
			while(i<x1.length && out){
				if(x1[i] != x2[i]){
					out = false;
				}
				i++;
			}
		}else{
			out = false;
		}
		return out;
	}

	public boolean hasKeyWithSameValue(String key,float value,String[] keyArray,float[] valueArray) {
		for(int i = 0;i<keyArray.length;i++){
			if(key.equals(keyArray[i])){
				if(value == valueArray[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the two vectors are the same
	 * @param key1
	 * @param values1
	 * @param key2
	 * @param values2
	 * @return
	 */
	public boolean sameVector(String[] key1,float[] values1,String[] key2,float[] values2){
		if(key1 == null && key2 != null){
			return false;
		}else if(key1 != null && key2 == null){
			return false;
		}else if(key1 == null && key2 == null){
			return true;
		}else{
			boolean out = true;
			if(key1.length == key2.length){
				int i = 0;
				while(out && i<key1.length){
					if(!hasKeyWithSameValue(key1[i],values1[i],key2,values2)){
						out = false;
					}
					i++;
				}
			}else{
				out = false;
			}
			return out;
		}
	}


	/**
	 * Restores a dense vector from a sparse vector
	 * @param indexes
	 * @param values
	 * @return
	 */
	float[] restoreVector(int[] indexes, float[] values) {
		float[] restoredVector = new float[dimXProj];
		if(indexes != null) {
			for(int i = 0;i < indexes.length;i++) {
				restoredVector[indexes[i]] = values[i];
			}
		}
		return restoredVector;
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
	 * Checks if (keyArray,valueArray) have the key "key" with the value "value"
	 * @param key
	 * @param value
	 * @param keyArray
	 * @param valueArray
	 * @return
	 */
	public boolean hasKeyWithSameValue(int key,float value,int[] keyArray,float[] valueArray) {
		for(int i = 0;i<keyArray.length;i++){
			if(key == keyArray[i]){
				if(value == valueArray[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if two sparse vectors are identical
	 * @param key1
	 * @param values1
	 * @param key2
	 * @param values2
	 * @return
	 */
	public boolean sameVector(int[] key1,float[] values1,int[] key2,float[] values2){
		if(key1 == null && key2 != null){
			return false;
		}else if(key1 != null && key2 == null){
			return false;
		}else if(key1 == null && key2 == null){
			return true;
		}else{
			boolean out = true;
			if(key1.length == key2.length){
				int i = 0;
				while(out && i<key1.length){
					if(!hasKeyWithSameValue(key1[i],values1[i],key2,values2)){
						out = false;
					}
					i++;
				}
			}else{
				out = false;
			}
			return out;
		}
	}


	/**
	 * Check if all the nodes' instances are clones with respect to their features or labels
	 * @param indexesX
	 * @param valuesX
	 * @param useInst
	 * @param keyY
	 * @param valuesY
	 */
	public void checkSameInt(ArrayList<int[]> indexesX,ArrayList<float[]> valuesX,ArrayList<Integer> useInst,ArrayList<String[]> keyY,ArrayList<float[]> valuesY) {


		int[] xIndexesCheck = indexesX.get(useInst.get(0));
		float[] xValuesCheck = valuesX.get(useInst.get(0));

		int i = 1;
		while(checkSameX && i < useInst.size()){
			if(!sameVector(xIndexesCheck,xValuesCheck, indexesX.get(useInst.get(i)),valuesX.get(useInst.get(i)))){
				checkSameX = false;
			}
			i++;
		}


		String[] yKeyCheck = keyY.get(useInst.get(0));
		float[] yValuesCheck = valuesY.get(useInst.get(0));


		i = 1;
		while(!checkSameX && checkSameY && i < useInst.size()){
			if(!sameVector(yKeyCheck,yValuesCheck, keyY.get(useInst.get(i)),valuesY.get(useInst.get(i)))){
				checkSameY = false;
			}
			i++;
		}

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

		if(isLeaf()){
			return nbInstance;
		}else {
			int total = 0;
			for(int i = 0; i < childrenInstancesValues.size();i++){
				total = total + children.get(i).getNbInstanceLeaves();
			}
			return total;
		}
	}


	public int getMaxInstanceLeaf(){
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
	}

	public int getMinInstanceLeaf(){
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
	}


	public int getNbLeaf(){

		if(isLeaf()){
			return 1;
		}else {
			int total = 0;
			for(int i = 0; i < childrenInstancesValues.size();i++){
				total = total + children.get(i).getNbLeaf();
			}
			return total;
		}
	}

	public int getNbNode(){

		if(isLeaf()){
			return 0;
		}else {
			int total = 0;
			for(int i = 0; i < childrenInstancesValues.size();i++){
				total = total + children.get(i).getNbNode();
			}
			return 1 + total;
		}
	}

	public int sumDepthLeaf(){

		if(isLeaf()){
			return floor;
		}else {
			int total = 0;
			for(int i = 0; i < childrenInstancesValues.size();i++){
				total = total + children.get(i).sumDepthLeaf();
			}
			return total;
		}
	}

	public int getMaxDepthLeaf(){
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
	}


	public int getMinDepthLeaf(){
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
	}



	@Override
	public void learn(SmallItem x, SmallItem y) {
		error("cannot call this function on this mode of CRAFTML");
	}



	@Override
	public boolean indicationFinDePasse() {
		error("cannot call this function on this mode of CRAFTML");
		return false;
	}



	@Override
	public boolean isTrained() {
		error("cannot call this function on this mode of CRAFTML");
		return false;
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

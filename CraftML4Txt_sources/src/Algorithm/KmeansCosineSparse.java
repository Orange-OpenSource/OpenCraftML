package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import Algorithm.VectorOperators;

/**
 * Algorithm of spherical kmeans initialized with kmeans++. The class is instantiated with a number of clusters, a set of sparse vectors, a number of pass and the dimension of the vectors. 
 * @author XPGT4620
 *
 */
public class KmeansCosineSparse {
	
	int nbClus;
	float[][] clusters;
	ArrayList<float[]> dataValues;
	ArrayList<int[]> dataIndexes;
	int nbPass;
	int nbInst;
	int dimInst;
	int[] indexBelong;
	
	public KmeansCosineSparse(int nbClus, ArrayList<float[]> dataValues, ArrayList<int[]> dataIndexes, int nbPass,int dimInst) {
		super();
		
		// les instances doivent etre normalisees
		
		this.nbClus = nbClus;
		this.dataValues = dataValues;
		this.dataIndexes = dataIndexes;
		this.nbPass = nbPass;
		
		this.nbInst = dataIndexes.size();
		this.dimInst = dimInst;
		
	}
	
	public float[][] getClusters(){
		return clusters;
	}
	
	public int[] getIndexClusters(){
		return indexBelong;
	}
	
	public void computeClusters() {
		initializeClusters();
		updateClusters();
	}
	
	public void initializeClusters(){
		
		//Initialisation avec kmeans++ 
		
		clusters = new float[nbClus][dimInst];
		
		int index = ThreadLocalRandom.current().nextInt(0, nbInst);
		
		int[] chosenClus = new int[nbClus];
		Arrays.fill(chosenClus, -1);
		
		chosenClus[0] = index;
		
		float[] currentValues = dataValues.get(index);
		int[] currentIndexes = dataIndexes.get(index);
		
		for(int i = 0;i<currentIndexes.length;i++){
			clusters[0][currentIndexes[i]] = currentValues[i];
		}
		
		float[] probaCum = new float[nbInst+1];
		
		float currentDist = 0;
		
		float currentRandom;
		
		for(int i = 1; i < nbClus;i++){
			for(int j = 0;j<nbInst;j++){
				currentDist = distCluster(j,i);
				probaCum[j+1] = probaCum[j] + currentDist*currentDist;
			}
			currentRandom = (float) (Math.random()*probaCum[nbInst]);
			
			
			index = getIndex(currentRandom,probaCum,0,nbInst);
			chosenClus[i] = index;
			
			currentValues = dataValues.get(index);
			currentIndexes = dataIndexes.get(index);
			
			for(int k = 0;k<currentIndexes.length;k++){
				clusters[i][currentIndexes[k]] = currentValues[k];
			}
		}
				
	}
	
	public void cleanClusters(){
		
		//fonction que l'on peut appeler après le calcul des clusters si lon souhaite detruire les clusters qui ont moins de la moitié de nbinst/nbclus
		
		int[] counts = new int[nbClus];
		for(int i = 0;i<indexBelong.length;i++){
			counts[indexBelong[i]]++;
		}
		boolean[] keep = new boolean[nbClus];
		for(int i = 0;i<nbClus;i++){			
			keep[i] = counts[i] > 0.5*nbInst/nbClus;
		}
		
		for(int i =0;i<nbInst;i++){
			indexBelong[i] = indexClusAmong(i, keep);
		}
		
	}
	
	
	public void updateClusters() {
		indexBelong = new int[nbInst];
		float[][] newClusters = new float[nbClus][dimInst];
		int[] nbInstClust = new int[nbClus];
		int indexClusterCurrent;
		
		for(int pass = 0; pass < nbPass; pass++){
			newClusters = new float[nbClus][dimInst];
			nbInstClust = new int[nbClus];			
			
			for(int i = 0;i<nbInst;i++){
				indexClusterCurrent = indexClus(i);
				indexBelong[i] = indexClusterCurrent;
				nbInstClust[indexClusterCurrent]++;
				
				float[] currentValues = dataValues.get(i);
				int[] currentIndexes = dataIndexes.get(i);
				
				for(int k = 0;k<currentIndexes.length;k++){
					newClusters[indexClusterCurrent][currentIndexes[k]] = newClusters[indexClusterCurrent][currentIndexes[k]] + currentValues[k];
				}
								
			}
			
			for(int i = 0;i<nbClus;i++){
				for(int j = 0;j<dimInst;j++){
					clusters[i][j] = newClusters[i][j]/nbInstClust[i];
				}	
			}
			
		}
	}
	
	
	public int getIndex(float value,float[] valuesArray,int indexDebut,int indexFin){
		
		if(indexDebut == indexFin-1){
			return indexDebut;
		}else{
			int indexCheck = Math.floorDiv(indexDebut+indexFin,2);
			if(value>valuesArray[indexCheck]){
				return getIndex(value, valuesArray, indexCheck, indexFin);
			}else{
				return getIndex(value, valuesArray, indexDebut, indexCheck);
			}
		}
	}
	
	
	public int indexClus(int indexInst){

		float[] instanceValues = dataValues.get(indexInst);
		int[] instanceIndexes = dataIndexes.get(indexInst);
		
		float minDist = Float.MAX_VALUE;
		int indexMin = 0;		
		float currentDistance;
		
		for(int i = 0;i<nbClus;i++){
			currentDistance = cosineDistance(instanceValues,instanceIndexes,clusters[i]);
			if(currentDistance<minDist){
				minDist = currentDistance;
				indexMin = i;
			}
		}
		
		return indexMin;
	}
	
	
	
	public int indexClusAmong(int indexInst,boolean[] useClus){
		
		float[] instanceValues = dataValues.get(indexInst);
		int[] instanceIndexes = dataIndexes.get(indexInst);
		
		float minDist = Float.MAX_VALUE;
		int indexMin = 0;		
		
		float currentDistance;
		
		for(int i = 0;i<nbClus;i++){
			if(useClus[i]){
				currentDistance = cosineDistance(instanceValues,instanceIndexes,clusters[i]);
				if(currentDistance<minDist){
					minDist = currentDistance;
					indexMin = i;
				}
			}			
		}
		
		return indexMin;
	}
	
	
	public float distCluster(int indexInst,int indexMaxClus){
		float[] instanceValues = dataValues.get(indexInst);
		int[] instanceIndexes = dataIndexes.get(indexInst);
		
		float minDist = Float.MAX_VALUE;
		float currentDistance;
		for(int i = 0;i<indexMaxClus;i++){
			currentDistance = cosineDistance(instanceValues,instanceIndexes,clusters[i]);
			if(currentDistance<minDist){
				minDist = currentDistance;
			}
		}
		return minDist;
	}
	
	public float cosineDistance(float[] valuesA, int[] indexesA, float[] b){
		/*float normA = 0;
		float normB = 0;*/
		
		float normA = 1;
		float normB = 1;
		
		float dot = 0;
		for(int i = 0; i < valuesA.length;i++){
			/*
			normA = normA + a[i]*a[i];
			normB = normB + b[i]*b[i];*/
			dot = dot + valuesA[i]*b[indexesA[i]];
		}
		return  (1f-dot/(((float) Math.sqrt(normA))*((float) Math.sqrt(normB))));
	}

	
}

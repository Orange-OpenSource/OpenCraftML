package FilesManagement;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import Algorithm.Displayer;


public class OnlinePerformanceCumulator {
	
	float nbOfExamples;
	float nbDimensionY;
	
	float fp,tp,fn,tn;
	
	String[] currentBestKeys;
	String idOfLastBestKeys = "eizfzojefehfhfcn";
	
	TreeMap<String,Float> computedPerformances;
	
	
	/** 
	 * constructor
	 * @param nbDimensionY
	 */
	public OnlinePerformanceCumulator(float nbDimensionY, List<String> Performances) {
		this.nbOfExamples = 0;
		this.nbDimensionY = nbDimensionY;
		this.computedPerformances = new TreeMap<String,Float>();
		for(int i = 0; i < Performances.size(); i++){
			this.computedPerformances.put(Performances.get(i), new Float(0));
		}
	}
	
	
	public OnlinePerformanceCumulator(List<String> Performances) {
		this.nbOfExamples = 0;
		this.nbDimensionY = 0;
		this.computedPerformances = new TreeMap<String,Float>();
		for(int i = 0; i < Performances.size(); i++){
			this.computedPerformances.put(Performances.get(i), new Float(0));
		}
	}
	
	
	
	public void computeCommonVals(SmallItem y, SmallItem yclass){
		//TODO ajouter la condition qu'une mesure qui en a besoin		
		tp = 0;
		fp = 0;
		fn = 0;
		tn = 0;
		for(String key:y.getKeySet()){
			if(yclass.getValue(key) !=0){
				tp++;
			}else{
				fn++;
			}
		}
		
		fp = ((float) yclass.getSize()) - tp;
		
		if(nbDimensionY != 0){
			tn = nbDimensionY - tp - fp - fn;		
		}
	}
	
	/**
	 * Add the prediction error to all performances 
	 * @param y
	 * @param ypred
	 */
	public void addError(SmallItem y, SmallItem ypred, SmallItem yclass){
		nbOfExamples++;
		computeCommonVals(y, yclass);
		
		for(String key:computedPerformances.keySet()){
			addErrorPerfo(y,ypred,yclass,key);
		}
	}
	
	
	public void addErrorPerfo(SmallItem y, SmallItem ypred,SmallItem yclass,String perfo){
		//TODO fonction a mettre a jour a lajout de perfo possible (optimiser les precisions et voir comment incorporer les trucs de rang)
		if(perfo.startsWith("Pat")){
			addPrecisionAtK(y, ypred, Integer.parseInt(perfo.substring(3)),perfo);
		}else{		
			switch(perfo){
			case "RMSE":
				addErrorRMSE(y, ypred,perfo);
			break;			
			case "Hamming Loss" :
				addErrorHammingLoss(perfo);
			break;
			case "Jaccard Loss" :
				addErrorJaccardLoss(perfo);
			break;
			case "Accuracy" :
				addErrorAccuracy(perfo);
			break;
			case "Precision" :
				addErrorPrecision(perfo);
			break;
			case "Recall" :
				addErrorRecall(perfo);
			break;
			case "Subset Accuracy" :
				//TODO renommer car ce n'est pas la bonne perfo
				addErrorSubsetAccuracy(perfo);
			break;
			case "F1 Score" :
				addErrorF1Score(perfo);
			break;
			}
		}
	}
	
	/**
	 * Add the prediction error to RMSE
	 * @param y
	 * @param ypred
	 */
	public void addErrorRMSE(SmallItem y, SmallItem ypred,String perfo){
		
		if(nbDimensionY != 0){
			SmallItem difference = SmallItem.getSparseVectorDifference(y, ypred);
			
			float squaredError = difference.getSparseVectorSumOfSquare();
			
			/*
			System.out.println("Squared Error = " + squaredError);
			
			System.out.println("Dim : " + nbDimensionY);*/
			
			float oldRMSE = computedPerformances.get(perfo);
			
			float newRMSE = (float) Math.sqrt((oldRMSE*oldRMSE*((nbOfExamples-1)*nbDimensionY) + squaredError)/(nbOfExamples*nbDimensionY));
			
					
			computedPerformances.put(perfo, newRMSE);
		}else{
			computedPerformances.put(perfo, 0f);
			Displayer.displayText("Warning : You cannot compute the RMSE without specifying the number of labels.");
		}
		
		
	}
	
	public void addPrecisionAtK(SmallItem y, SmallItem ypred, int k,String perfo){

		//TODO attention valeur negative dans ypred
		
		float cumul = 0;
		
		if(ypred.getKeyArray() != null && ypred.getKeyArray().length >= k){
			
			//TODO attention quand on cherche precision + de 10
			String[] bestKeys = getBestKeys(ypred,k); 
			
			for(int i = 0; i < k; i++){
				if(y.getValue(bestKeys[i]) != 0){
					cumul = cumul + 1;
				}
			}
 		}else{
 			if(ypred.getKeyArray() != null){
 				for(String key:ypred.getKeyArray()){
 					if(y.getValue(key) != 0 && ypred.getValue(key) > 0){
 						cumul = cumul + 1;
 					}
 				}
 			}
 		}
		
		float newPAtK = (computedPerformances.get(perfo)*(nbOfExamples - 1) + cumul/((float) k))/nbOfExamples; 
		computedPerformances.put(perfo, newPAtK);
	}
	
	static public String[] getBestKeys(SmallItem ypred,int k){
	
		Pair[] bestScoreAndId = new Pair[k];
		
		
		String[] keyArray = ypred.getKeyArray();
		
		int lowerValueIndexCache = 0;			
		float lowerValueCache = 0;
		float currentLabelValue;
		
		for(int i = 0;i < k; i++){
			currentLabelValue = ypred.getValue(keyArray[i]);
			
			bestScoreAndId[i] = new Pair(keyArray[i], currentLabelValue);

			if(i==0){
				lowerValueCache = currentLabelValue;
				lowerValueIndexCache = i;
			}else{
				if(currentLabelValue < lowerValueCache){
					lowerValueCache = currentLabelValue;
					lowerValueIndexCache = i;
				}
			}
		}
		
		for(int i = k;i < keyArray.length; i++){
			currentLabelValue = ypred.getValue(keyArray[i]);
			if(currentLabelValue > lowerValueCache){
				bestScoreAndId[lowerValueIndexCache] = new Pair(keyArray[i], currentLabelValue);
				lowerValueIndexCache = smallestItemIndex(bestScoreAndId);
				lowerValueCache = bestScoreAndId[lowerValueIndexCache].value;					
			}	
		}
		
		Arrays.sort(bestScoreAndId);
		
		String[] bestKeys = new String[k];
		for(int i = 0;i < k; i++){
			bestKeys[i] = bestScoreAndId[i].index;
		}
		
		return bestKeys;
	}
	
	public static int smallestItemIndex(Pair[] bestScoreAndId){
		float minVal = bestScoreAndId[0].value;
		int minIdx = 0; 
		for(int idx=1; idx<bestScoreAndId.length; idx++) {
			if(bestScoreAndId[idx].value < minVal) {
				minVal = bestScoreAndId[idx].value;
				minIdx = idx;
			}
		}		
		return minIdx;
	}
	
	public void addErrorHammingLoss(String perfo){		
		if(nbDimensionY!=0){
			float hammingDist = (fp + fn)/nbDimensionY;
			float newHammingLoss = (computedPerformances.get(perfo)*(nbOfExamples-1) + hammingDist)/(nbOfExamples);		
			computedPerformances.put(perfo, newHammingLoss);
		}else{
			computedPerformances.put(perfo, 0f);
			Displayer.displayText("Warning : You cannot compute the Hamming Loss without specifying the number of labels.");
		}		
	}
	
	
	public void addErrorJaccardLoss(String perfo){
		float jaccardDist;
		//TODO verifier les valeurs par défaut
		
		if((fn+fp+tp)!=0){
			jaccardDist = (fp + fn)/(fp + fn + tp);
		}else{
			jaccardDist = 0;
		}
		float newJaccardLoss = (computedPerformances.get(perfo)*(nbOfExamples-1) + jaccardDist)/(nbOfExamples);		
		computedPerformances.put(perfo, newJaccardLoss);
	}
	
	public void addErrorAccuracy(String perfo){
		float accu = tp/(fp + fn + tp);
		if((fn+fp+tp)!=0){
			accu = tp/(fp + fn + tp);
		}else{
			accu = 1;
		}
		float newAccuracy = (computedPerformances.get(perfo)*(nbOfExamples-1) + accu)/(nbOfExamples);	
		computedPerformances.put(perfo, newAccuracy);
	}
	
	public void addErrorPrecision(String perfo){
		float prec;
		if((fp+tp)!=0){
			prec = tp/(tp + fp);
		}else{
			prec = 1;
		}
		float newPrecision = (computedPerformances.get(perfo)*(nbOfExamples-1) + prec)/(nbOfExamples);	
		computedPerformances.put(perfo, newPrecision);
	}
	
	public void addErrorRecall(String perfo){
		float reca;
		if((fn+tp)!=0){
			reca = tp/(tp + fn);
		}else{
			reca = 1;
		}
		float newRecall = (computedPerformances.get(perfo)*(nbOfExamples - 1) + reca)/(nbOfExamples);
		computedPerformances.put(perfo, newRecall);
	}
	
	public void addErrorSubsetAccuracy(String perfo){
		float subs = tp;
		float newSubsetAccuracy = (computedPerformances.get(perfo)*(nbOfExamples - 1) + subs)/(nbOfExamples);
		computedPerformances.put(perfo, newSubsetAccuracy);
	}
	
	public void addErrorF1Score(String perfo){
		float f1;
		if((fp + fn + 2*tp)!=0){
			f1 = 2*tp/(fp + fn + 2*tp);
		}else{
			f1 = 1;
		}
		float newF1Score = (computedPerformances.get(perfo)*(nbOfExamples - 1) + f1)/(nbOfExamples);
		computedPerformances.put(perfo, newF1Score);
	}
	
	/**
	 * returns the performance specified in the string perfo
	 * @param perfo
	 * @return
	 */
	public float getPerformance(String perfo){		
		float out = computedPerformances.get(perfo);	
		return out;
	}
	
	
}

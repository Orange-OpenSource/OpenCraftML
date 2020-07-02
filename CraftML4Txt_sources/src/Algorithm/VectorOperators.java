package Algorithm;


import java.util.TreeMap;


import FilesManagement.SmallItem;

public class VectorOperators {
	
	public static void error(String mess) {
		Displayer.displayText("Error:\n" + mess);
		Error er = new Error(mess);
		throw er;
	}
	
	/**
	 * Error when the entries are null, or of size = 0
	 * @param H
	 * @param Y
	 */
	// ..........................  Tested .....................................
	public static void NullSparseVector(SmallItem H, SmallItem Y){
		if(H == null || Y == null){
			error("vectors == null\n");
		}
	}
	
	/**
	 * Error when the entry is null, or size = 0
	 * @param H
	 */
	// ................................ Tested ...........................
	public static void NullSparseVector(SmallItem H){
		if(H == null ){
			error("vector == null");
		}
	}
	
	static public float[] getArrayFromSparseVector(SmallItem w, TreeMap<String,Integer>correspondance){
		int dimension = correspondance.size();
		float[] result = new float[dimension];
		
		/*
		if(!w.positifIntKeys()){
			error("Sparse vector doesn't have positive integer column keys");
		}
		if(w.getSize() == 0){
			return result;
		}
		*/
		for(String key:correspondance.keySet()){
			result[correspondance.get(key)] = w.getValue(key);
		}
		return result;
	}
	
	
	/**
	 * Transformer un sparse vector en tableau
	 * @param w
	 * @param dimension
	 * @return
	 */
	/*
	static public float[] getArrayFromSparseVector(SmallItem w, int dimension){
		float[] result = new float[dimension];
		
		
		if(!w.positifIntKeys()){
			error("Sparse vector doesn't have positive integer column keys");
		}
		if(w.getSize() == 0){
			return result;
		}
		
		int p = 0;
		for(String key:w.getKeyArray()){
			result[p] = w.getValue(key);
			p ++ ;
		}
		return result;
	}
	*/
	/**
	 * Produit d'un sparseVector par un scalaire.
	 * @param vector
	 * @param scalar
	 * @return
	 */
	// .................................. Tested ..........................
	static public SmallItem product(SmallItem vector, float scalar){
		NullSparseVector(vector);
		SmallItem product = new SmallItem();
		if(vector.getID() != null){
			product.setID(vector.getID());
		}
		if(vector.getSize() == 0){
			return vector;
		}
		for(String key:vector.getKeyArray()){
			product.putKeyValue(key, scalar*vector.getValue(key));
		}
		return product;
	}
	
	/**
	 * somme d'un sparse vecteur et d'un scalaire.
	 * @param vector
	 * @param scalar
	 * @param NbrLabel
	 * @return
	 */
	static public SmallItem sum(SmallItem vector, float scalar, TreeMap<String,Integer>correspondance){
		NullSparseVector(vector);
		SmallItem somme = new SmallItem();
		if(vector.getID() != null){
			somme.setID(vector.getID());
		}
		for(String key : correspondance.keySet()){
			somme.putKeyValue(key, scalar+vector.getValue(key));
		}
		return somme;
	}
	
	/**
	 * Produit d'un vecteur par un scalaire
	 * @param vector
	 * @param scalar
	 * @return
	 */
	static public float[] product(float[] vector, float scalar){
		if(vector == null){
			return null;
		}
		float[] product = new float[vector.length];
		for (int i = 0; i < vector.length; i++) {
			product[i] = vector[i]*scalar;
		}
		return product;
	}
	
	/**
	 * Somme de deux sparseVector
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	// .............................. Tested ..................................
	static public SmallItem sum(SmallItem vector1, SmallItem vector2){
		NullSparseVector(vector1,vector2);
		SmallItem sum = new SmallItem();
		if(vector1.getSize() == 0){
			return vector2;
		}
		if(vector2.getSize() == 0){
			return vector1;
		}
		for(String key : vector1.getKeyArray()){
			if(vector2.hasKey(key)){
				sum.putKeyValue(key, vector1.getValue(key)+vector2.getValue(key));
			}
			else{
				sum.putKeyValue(key, vector1.getValue(key));
			}
		}
		for(String key : vector2.getKeyArray()){
			if(!vector1.hasKey(key)){
				sum.putKeyValue(key,vector2.getValue(key));
			}
		}
		return sum;
	}
	
	/**
	 * Somme d'un tableau de réel et d'un vecteur sparse
	 * @param vector1 : tableau de réel
	 * @param vector2 : vecteur sparse
	 * @return
	 */
	static public float[] sum(float[] vector1, SmallItem vector2, TreeMap<String,Integer> correspondance){
		NullSparseVector(vector2);
		
		if(vector1.length < correspondance.size()){
			error("Dimensions don't match");
		}
		if(vector2.getSize() == 0){
			return vector1;
		}
		float[] sum = vector1.clone();
		for(String key : correspondance.keySet()){
			sum[correspondance.get(key)] = vector1[correspondance.get(key)]+ vector2.getValue(key);
		}
		return sum;
	}
	
	/**
	 * Somme de deux tableaux réels
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	static public float[] sum(float[] vector1, float[] vector2){
		if(vector1.length != vector2.length){
			error("Invalid dimension vector");
		}
		if(vector1.length == 0){
			return vector2;
		}
		if(vector2.length == 0){
			return vector1;
		}
		float[] vector = new float[vector1.length];
		for(int i=0; i<vector1.length;i++){
			vector[i] = vector1[i]+vector2[i];
		}
		return vector;
	}
	static public float[] difference(float[] vector1, float[] vector2){
		return sum(vector1,product(vector2,-1));
	}
	
	/**
	 * difference entre un tableau de réel et un vecteur sparse
	 * @param vector1 : tableau de réel
	 * @param vector2 : vecteur sparse
	 * @return
	 */

	static public float[] difference(float[] vector1, SmallItem vector2, TreeMap<String, Integer> correspondance){
		return sum(vector1,product(vector2,-1),correspondance);
	}
	
	/**
	 * Différence de deux sparsevector
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	// .............................. Tested ..................................

	static public SmallItem difference(SmallItem vector1, SmallItem vector2){
		NullSparseVector(vector1,vector2);
		return sum(vector1,product(vector2,-1));
	}
	
	/**
	 * Produit scalaire entre deux vecteurs sparses
	 * @param vector1 : vecteur sparse
	 * @param vector2 : vecteur sparse
	 * @return
	 */
	
	// ................................. Tested ..........................
	static public float dotProduct(SmallItem vector1, SmallItem vector2){
		NullSparseVector(vector1,vector2);
		float dotProduct = 0;
		if(vector1.getSize() == 0 || vector2.getSize() == 0){
			return 0;
		}
		else {
			for(String key : vector1.getKeyArray()){
				if(vector2.hasKey(key)){
					dotProduct += vector1.getValue(key)*vector2.getValue(key);
				}
			}
			return dotProduct;
		}
		
	}
	
	/** 
	 * Produit scalaire entre deux vecteurs
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	static public float dotProduct(float[] vector1, float[] vector2){
		if(vector1.length != vector2.length){
			error("Invalid dimension vector");
		}
		if(vector1.length == 0 || vector2.length == 0){
			return 0;
		}
		float dotProduct = 0;
		for(int i=0; i< vector1.length; i++){
			dotProduct+=vector1[i]*vector2[i];
		}
		return dotProduct;
	}
	
	/**
	 * Norme euclidienne calculée pour un vecteur sparse
	 * @param vector : vecteur sparse
	 * @return
	 */
	// ............................. Tested .........................
	static public float norm_L2(SmallItem vector){
		return (float) Math.sqrt(dotProduct(vector,vector));
	}
	
	static public float norm_L2(float[] vector){
		return (float) Math.sqrt(dotProduct(vector,vector));
	}
	
	/**
	 * Norme 1  calculée pour un vecteur sparse
	 * @param vector : vecteur sparse
	 * @return
	 */
	// ............................. Tested ...........................
	static public float norm_L1(SmallItem vector){
		NullSparseVector(vector);
		if(vector.getSize() == 0){
			return 0;
		}
		float norm_L1 = 0;
		for(String key : vector.getKeyArray()){
			norm_L1 += Math.abs(vector.getValue(key));
		}
		return norm_L1;
	}
	
	static public float norm_L1(float[] vector){
		if(vector.length == 0){
			error("vector null");
		}
		float norm_L1 = 0;
		for(int i=0; i<vector.length;i++){
			norm_L1 += Math.abs(vector[i]);
		}
		return norm_L1;
	}
	
	/**
	 * Norme de holder d'ordre p calculée pour un vecteur sparse
	 * @param vector : vecteur sparse
	 * @param p : ordre de la norme de Holder
	 * @return
	 */
	// ........................ Tested   ................................
	static public float normHolder(SmallItem vector, int p){
		NullSparseVector(vector);
		if(vector.getSize() == 0){
			return 0;
		}
		if(p<=0){
			error("Indice de holder nul ou négatif");
		}
		float normHolder = 0;
		for(String key : vector.getKeyArray()){
			normHolder += (float) Math.abs(Math.pow(vector.getValue(key),p));
		}
		return (float) Math.pow(normHolder,1.0/p);
	}
	
	static public float normHolder(float[] vector, int p){
		if(vector.length == 0){
			error("vector null");
		}
		if(p<=0){
			error("Indice de holder nul ou négatif");
		}
		float normHolder = 0;
		for(int i=0; i<vector.length;i++){
			normHolder += (float) Math.abs(Math.pow(vector[i],p));
		}
		return (float) Math.pow(normHolder,1.0/p);
	}
	
	/**
	 * distance de Manhattan entre deux vecteurs sparses
	 * @param vector1 : vecteur sparse
	 * @param vector2 : vecteur sparse
	 * @return
	 */
	// .......................... Tested ............................
	static public float Distance_L1(SmallItem vector1, SmallItem vector2){
		return norm_L1(difference(vector1,vector2));
	}
	
	static public float Distance_L1(float[] vector1, float[] vector2){
		return norm_L1(difference(vector1,vector2));
	}
	
	// return 1 if there ias a perfect match 0 otherwise
	static public int equals(SmallItem vector1, SmallItem vector2){
		SmallItem diff = difference(vector1,vector2);
		int res = (diff.getSize() == 0) ? 1 : 0;
		return res;
				
	}
	
	/**
	 * Distance euclidienne entre deux vecteurs sparses
	 * @param vector1 : vecteur sparse
	 * @param vector2 : vecteur sparse
	 * @return
	 */
	// ............................  Tested   .................................
	/*
	static public float Distance_L2(SmallItem vector1, SmallItem vector2){
		return norm_L2(difference(vector1,vector2));
	}
	*/
	
	static public float Distance_L2(float[] vector1, float[] vector2){
		if(vector1.length != vector2.length){
			error("Invalid dimension vector");
		}
		if(vector1.length == 0){
			return 0;
		}
		float distance = 0;
		for(int i=0; i<vector1.length;i++){
			distance += Math.pow(vector1[i] - vector2[i] , 2);
		}
		return (float) Math.sqrt(distance);
	}
	
	
	
	static public double Distance_L2(double[] vector1, double[] vector2){
		if(vector1.length != vector2.length){
			error("Invalid dimension vector");
		}
		if(vector1.length == 0){
			return 0;
		}
		double distance = 0;
		for(int i=0; i<vector1.length;i++){
			distance += Math.pow(vector1[i] - vector2[i] , 2);
		}
		return Math.sqrt(distance);
	}
	
	
	static public float Distance_L2SQ(float[] vector1, float[] vector2){
		if(vector1.length != vector2.length){
			error("Invalid dimension vector");
		}
		if(vector1.length == 0){
			return 0;
		}
		float distance = 0;
		for(int i=0; i<vector1.length;i++){
			float val = vector1[i] - vector2[i];
			distance += val*val;
		}
		return distance;
	}
	
	
	static public float Distance_L2(SmallItem vector1, SmallItem vector2){
		float distance = 0 ;
		if(vector1.getSize() == 0 && vector2.getSize() == 0){
			return distance;
		}
		else if(vector1.getSize() != 0 && vector2.getSize() == 0){
			for(String key : vector1.getKeySet()){
				distance += Math.pow(vector1.getValue(key), 2);
			}
			return (float) Math.sqrt(distance);
		}
		else if(vector1.getSize() == 0 && vector2.getSize() != 0){
			for(String key : vector2.getKeySet()){
				distance += Math.pow(vector2.getValue(key), 2);
			}
			return (float) Math.sqrt(distance);
		}
		else{
			for(String key : vector1.getKeySet()){
				if(vector2.hasKey(key)){
					distance += Math.pow(vector1.getValue(key) - vector2.getValue(key), 2);
				}
				else{
					distance += Math.pow(vector1.getValue(key), 2);
				}
			}
			for(String key : vector2.getKeySet()){
				if(!vector1.hasKey(key)){
					distance += Math.pow(vector2.getValue(key), 2);
				}
			}
			return (float) Math.sqrt(distance);
		}
		
	}
		
	
	
	
	/**
	 * Calcul de la distance de Holder d'ordre p entre deux vecteurs sparses
	 * @param vector1 : vecteur sparse
	 * @param vector2 : vecteur sparse
	 * @param p : ordre de Holder
	 * @return
	 */
	// ............................ Tested .............................
	static public float DistanceHolder(SmallItem vector1, SmallItem vector2, int p){
		return normHolder(difference(vector1,vector2),p);
	}
	
	static public float DistanceHolder(float[] vector1, float[] vector2, int p){
		return normHolder(difference(vector1,vector2),p);
	}
	
	/**
	 * Distance de Hamming entre deux vecteurs sparses
	 * @param H : vecteur sparse 
	 * @param Y : vecteur sparse
	 * @param CardL : cardinal des vecteurs sparses
	 * @return
	 */
	// ........................ Tested ..............................
	static public float HammingDistance(SmallItem H, SmallItem Y, int CardL){
		NullSparseVector(H,Y);
		if(H.getSize() > CardL || Y.getSize() > CardL || CardL <=0){
			error("Dimension of sparse vector invalid");
		}
		if(H.getSize() == 0 && Y.getSize() == 0){
			return 0;
		}
		if(H.getSize() == 0){
			return ((float)Y.getSize())/CardL;
		}
		if(Y.getSize() == 0){
			return ((float)H.getSize())/CardL;
		}
		//TODO voir si on garde
		/*
		if(!H.isBinary() ||!Y.isBinary()){
			error("Un des vecteurs sparses en entrée est non binaire"+"\n"+"Distance de Hamming non calculable pour des vecteurs non binaires");
		}*/
		float HammingDistance = 0;
		for(String key : H.getKeyArray()){
			if(!Y.hasKey(key)){
				HammingDistance+=1;
			}
		}
		for(String key : Y.getKeyArray()){
			if(!H.hasKey(key)){
				HammingDistance+=1;
			}
		}
		return HammingDistance/CardL;
	}
	
	/**
	 * Distance de jaccard entre deux vecteurs sparses H et Y
	 * @param H
	 * @param Y
	 * @return
	 */
	// ...................... Tested .........................
	public static float JaccardDistance(SmallItem H, SmallItem Y){
		NullSparseVector(H,Y);
		float M11 = 0;
		float M10 = 0;
		float M01 = 0;
		/*
		if(!H.isBinary() ||!Y.isBinary()){
			error("Un des vecteurs sparses en entrée est non binaire"+"\n"+"Distance de Jaccard non calculable pour des vecteurs non binaires");
		}
		*/
		if(H.getSize() != 0){
			for(String key : H.getKeyArray()){
				if(Y.hasKey(key)){
					M11+=1;
				}
				else{
					M10+=1;
				}
			}
		}
		if(Y.getSize() != 0){
			for(String key : Y.getKeyArray()){
				if(!H.hasKey(key)){
					M01+=1;
				}
			}
		}
		if(M01+M10+M11  == 0){
			return 0;
		}
		else
			return (M01+M10)/(M01+M10+M11);
	}
	
	/**
	 * Indice de Jaccard entre deux vecteurs sparses H et Y
	 * @param H
	 * @param Y
	 * @return
	 */
	// ........................  Tested  ..............................
	public static float JaccardIndex(SmallItem H, SmallItem Y){
		NullSparseVector(H,Y);
		float M11 = 0;
		float M10 = 0;
		float M01 = 0;
		//TODO voir si on garde
		/*
		if(!H.isBinary() ||!Y.isBinary()){
			error("Un des vecteurs sparses en entrée est non binaire"+"\n"+"Indice de Jaccard non calculable pour des vecteurs non binaires");
		}*/
		if(H.getSize() ==0 && Y.getSize() ==0){
			return 0;
		}
		else{
			if(H.getSize() !=0)
			{
				for(String key : H.getKeyArray()){
					if(Y.hasKey(key)){
						M11+=1;
					}
					else{
						M10+=1;
					}
				}
			}
			if(Y.getSize() !=0)
			{
				for(String key : Y.getKeyArray()){
					if(!H.hasKey(key)){
						M01+=1;
					}
				}
			}
		}
		
		return M11/(M01+M10+M11);
	}
	
	/**
	 * Génération d'un vecteur selon une loi uniforme
	 * @param column : taille du vecteur
	 * @param a : limite inférieure du segment
	 * @param b : limite supérieure du segment
	 * @return
	 */
	//.............................Tested..............................
	public static float[] generateUniformVector(int column, float a, float b){
		if (a>b){
			float c = b; b = a;a=c;
		}
		float[] vector = new float[column];
		for(int i=0; i<column; i++){
			vector[i] = a +(b-a)*(float)Math.random();
		}
		return vector;
	}
	
	
	
	public static float[] signVector(float[] vector){
		float[] signVec = new float[vector.length];
		for(int i = 0; i<vector.length;i++){
			signVec[i] = (float) Math.signum(vector[i]);
		}
		return signVec;
	}
	
}

package FilesManagement;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import Algorithm.Displayer;


/**
 * Classe SparseVector draft, ossature;..
 * @author ofmu6285
 *
 */
public class SmallItem {

	/**
	 * association clé-valeur  ; pour le moment, choix d'un String comme ID (pourrait être remplacé par un Integer facilement)
	 */
	private TreeMap<String, Float>  keyValue; 
	
	private String ID="";

	public void setID(String id) {
		if (id==null) {
			error("id is null");
		}
		this.ID=id;
	}
	
	public String getID(){
		return ID;
	}
	
	public Set<String> getKeySet(){
		return keyValue.keySet();
	}
	

	public static void error(String mess) {
		Displayer.displayText("Error:\n" + mess);
		Error er = new Error(mess);
		throw er;
	}
	
	/**
	 * retourne un tableau avec les clés, retourne null s'il n'y a pas de clés
	 * @return
	 */
	public String[] getKeyArray() {
		if (this.keyValue.size()==0) {
			return null;
		}
		String[] keys;
		keys= new String[this.keyValue.size()];
		int i=0;
		for (String key:this.keyValue.keySet()) {
			keys[i]=key;
			i++;
		}
		return keys;
	}
	
	public void putKeyValue(String key, float value) {
		if (key==null) {
			error("key is null");
		}
		key=key.trim();
		if (key.equals("")) {
			error("key is empty string");
		}
		if (value!=0) {
			keyValue.put(key, value);
		} else {
			keyValue.remove(key);
		}
	}
	
	public float getValue(String key) {
		if (key==null) {
			error("key is null");
		}
		key=key.trim();
		if (key.equals("")) {
			error("key is empty string");
		}
		Float f=keyValue.get(key);
		if (f==null) {
			return 0;
		}
		return f;
	}
	

	public static void warning(String mess) {
		Displayer.displayText("SparseVectorDraft warning:\n"+mess);
		//
		Error er=new Error();
		er.printStackTrace();
	}

	/**
	 * Constructeur simple
	 */
	public SmallItem() {
		keyValue = new TreeMap<String, Float>();
	}
	
	
	/**
	 * retourne la liste des K features les plus élévés
	 * 
	 * @param reference
	 * @return
	 */
	public BestIndexValueContener getHigestFeature(int k) {
		
		BestIndexFinderInOnePass myIndexFinder= new BestIndexFinderInOnePass(k);
		
		String[] keyArray = getKeyArray();		
		
		int i =0;
		
		
		for (String key:keyArray) {
			float best=getValue(key);
			myIndexFinder.add(i, best);
			i++;
		}
		
		myIndexFinder.bubbleSort();
		
		BestIndexValueContener result=new BestIndexValueContener(myIndexFinder.bestID, myIndexFinder.bestScore, keyArray);
		
		/**
		TreeMap<Integer, Float> result=new TreeMap<Integer, Float>();
		for (int i=0;i<k;i++) {
			int index=myIndexFinder.bestID[i];
			result.put(index, this.feature[index]);  //  ALTERNATIVE VRAIE VALEUR
			
		//	result.put(index,myIndexFinder.bestScore[i]);
		}
		
		*/
		
		return result;
	}
	

	/**
	 * fonction utilitaire : test float oui/non
	 * @param number
	 * @return
	 */
	static public boolean isAFloat(String number) {
		boolean isANumber;
		double d;
		try {
			d=Float.parseFloat(number);
			isANumber=true;
		} catch(NumberFormatException e) {
			isANumber=false;
		}
		return isANumber;
	}	
	
	
	public SmallItem topN(int n){
		if(getKeyArray() != null && getKeyArray().length >= n){
			String[] bestKeys = OnlinePerformanceCumulator.getBestKeys(this,n); 
			SmallItem out = new SmallItem();
			for(int i = 0; i < n; i++){
				out.putKeyValue(bestKeys[i], this.getValue(bestKeys[i]));
			}
			return out;
		}else{
			return getClone();
		}
		
	}
	

	/**
	 * initialise via un record de type <index : value>
	 * @param record
	 */
	public void initViaLineIndexValue(String record, String fieldSeparator, String valueSeparator) {
		
		if (record==null) {
			error("line is null");
		}
		if (fieldSeparator==null) {
			error("field sep is null");
		}
		if (valueSeparator==null) {
			error("value sep is null");
		}
		if (fieldSeparator.length()<1) {
			error("field sep is empty string");
		}
		if (valueSeparator.length()<1) {
			error("value sep is empty string");
		}
		if (keyValue.size()!=0) {
			error("the vector is already initialized : "+getLinetext(";", "="));
		}
		String[] fields=record.split(fieldSeparator) ;
		if (fields.length==0) {
			error("no field for line :"+record);
		}
		for (int i=0; i<fields.length;i++) {
			String indexValue[]=fields[i].split(valueSeparator);
			if (indexValue.length!=2) {
				//Displayer.displayText("line :"+record);
				//Displayer.displayText("field :"+fields[i]);
				//Displayer.displayText("object with id : " + ID + " has no values");
				//TODO verifier si c'est un warning 
				
			} else {
				boolean ok=true;
				indexValue[0]=indexValue[0].trim();
				indexValue[1]=indexValue[1].trim();
				if (indexValue[0].length()<1) {
					Displayer.displayText("line :"+record);
					Displayer.displayText("field :"+fields[i]);
					warning("wrong index");
					ok=false;
				}
				if (!isAFloat(indexValue[1])) {
					Displayer.displayText("line :"+record);
					Displayer.displayText("field :"+fields[i]);
					warning(" wrong value :"+indexValue[1]);
					ok=false;
				}
				if (ok) {
					float f=Float.parseFloat(indexValue[1]);
					if (f!=0) {
						this.putKeyValue(indexValue[0], f);
					}
				}
			}
		}

	}

	/**
	 * retourne un copie profonde (clone) du vecteur
	 * @return
	 */
	public SmallItem getClone() {
		SmallItem myClone=new SmallItem();
		for (String key:this.keyValue.keySet()) {
			Float value=this.keyValue.get(key);
			if (value!=0) {
				myClone.keyValue.put(key, value);
			}
		}
		return myClone;
	}


	/**
	 * renvoie une ligne text de type <index : value> 
	 * @param fieldSeparator
	 * @param valueSeparator
	 * @return
	 */

	public String getLinetext(String fieldSeparator, String valueSeparator) {
		if (fieldSeparator==null) {
			error("field sep is null");
		}
		if (valueSeparator==null) {
			error("value sep is null");
		}
		if (fieldSeparator.length()<1) {
			error("field sep is empty string");
		}
		if (valueSeparator.length()<1) {
			error("value sep is empty string");
		}
		String result="";
		int size=this.keyValue.size();
		int entryNumber=1;
		for (String key:this.keyValue.keySet()) {
			Float value=keyValue.get(key);
			result=result+key+valueSeparator+value;
			if (entryNumber<size) {
				result=result+fieldSeparator;
			}
		}
		return result;
	}
	
	
	/**
	 * renvoie une ligne text de type <index : value> avec les 0 rangées dans le meme ordre que le tableau de string reçu 
	 * @param fieldSeparator
	 * @param valueSeparator
	 * @return
	 */

	public String getOrderedLinetext(String fieldSeparator, String valueSeparator,String[] variableArray) {
		if (fieldSeparator==null) {
			error("field sep is null");
		}
		if (valueSeparator==null) {
			error("value sep is null");
		}
		if (fieldSeparator.length()<1) {
			error("field sep is empty string");
		}
		if (valueSeparator.length()<1) {
			error("value sep is empty string");
		}
		//TODO checker erreur variable array
		
		String result="";
		int size=variableArray.length;
		int entryNumber=1;
		
		for (int i = 0; i < variableArray.length;i++) {
			Float value=keyValue.get(variableArray[i]);
			result=result+variableArray[i]+valueSeparator+value;
			if (entryNumber<size) {
				result=result+fieldSeparator;
			}
		}
		return result;
	}
	

	/**
	 * Génère un vecteur aléatoire ; les précédentes key-value sont supprimées
	 * @param indexMax  : taille max du vecteur ; on suppose que les index commencent à 1 et finissent à indexMax
	 * @param ratio  : proba d'une valeur 1
	 */

	public void generateRandomBinaryVector(int indexMax, double ratio) {
		this.keyValue.clear();
		for (int i=1; i<=indexMax; i++) {
			if (Math.random()<ratio) {
				this.putKeyValue("x"+i, 1f);
			}
		}
	}


	/** 
	 * retourne le cosinus de 2 sparse Vector x et y ; 
	 * les entrées nulles en x ou en y ne sont pas considérées
	 * @param x
	 * @param y
	 * @return
	 */
	public static float getSparseVectorCosine(SmallItem x, SmallItem y)
	{
		float distNum=0;  float normX=0; float normY=0;
		// perform x.y and normX
		Iterator<String> itx;
		itx=x.keyValue.keySet().iterator();
		while (itx.hasNext()) {
			String xkey=(String)itx.next();
			float xvalue=(float)x.keyValue.get(xkey) ;
			normX=normX+xvalue*xvalue;
			float yvalue;
			if (y.keyValue.get(xkey)==null) {
				yvalue=0;

			} else {
				yvalue=(float) y.keyValue.get(xkey);

			}
			distNum=distNum+xvalue*yvalue;
		}

		// perform normY
		Iterator<String> ity;
		ity=y.keyValue.keySet().iterator();
		while (ity.hasNext()) {
			String ykey=(String)ity.next();
			float yvalue=(float)y.keyValue.get(ykey) ;
			normY=normY+yvalue*yvalue;
		}

		float cos;
		if (normX!=0 && normY!=0) {
			cos=distNum/(float)(Math.sqrt(normX)*Math.sqrt(normY));
		} else {
			cos=0;
		}
		return cos;
	}
	
	
	public int getSize(){
		if(keyValue != null){
			return keyValue.size();
		}else{
			return 0;
		}
	}
	
	public boolean hasKey(String key){
		return keyValue.containsKey(key);
	}
	
	
	/** 
	 * retourne la difference de 2 sparse Vector x et y ; 
	 * les entrées nulles en x ou en y ne sont pas considérées
	 * @param x
	 * @param y
	 * @return
	 */
	public static SmallItem getSparseVectorDifference(SmallItem vector1, SmallItem vector2){

		if(vector1 == null || vector2 == null){
			Displayer.displayText("vectors == null\n");
			int a = 0/0;
		}
		
		SmallItem diff = new SmallItem();
		
		if(vector1.getSize() == 0){
			if(vector2.getSize() == 0){
				return new SmallItem();
			}else{
				for(String key : vector2.getKeyArray()){
					diff.putKeyValue(key,-vector2.getValue(key));
				}
				return diff;
			}
		}
		
		if(vector2.getSize() == 0){
			return vector1.getClone();
		}
		
		for(String key : vector1.getKeyArray()){
			if(vector2.hasKey(key)){
				diff.putKeyValue(key, vector1.getValue(key)-vector2.getValue(key));
			}
			else{
				diff.putKeyValue(key, vector1.getValue(key));
			}
		}
		for(String key : vector2.getKeyArray()){
			if(!vector1.hasKey(key)){
				diff.putKeyValue(key,-vector2.getValue(key));
			}
		}
		return diff;
	}

	/**
	 * Returns sum of vector's elements' square
	 * @return
	 */
	public float getSparseVectorSumOfSquare(){
		float sumOfSquare = 0;
		if(getKeyArray() != null){
			for(String key : getKeyArray()){
				sumOfSquare = sumOfSquare + getValue(key)*getValue(key);
			}
		}		
		return sumOfSquare;
	}
	
	
	public void deleteReferenceKeys(SmallItem ref) {
		String[] keysToDelete=ref.getKeyArray();
		for (int i=0;i<keysToDelete.length;i++) {
			this.keyValue.remove(keysToDelete[i]);
		}
	}
	
	
	public static void main(String[] p) {


		String fieldSep=";";
		String valueSep="=";
		String record1;

		Displayer.displayText("----- création / recopie --------");
		SmallItem myVector1= new SmallItem();

		record1=" a=1.5   ; b=2 ; c=3;   z=800.01";

		System.out.println(" record1 : "+record1);
		myVector1.initViaLineIndexValue(record1, fieldSep, valueSep);

		String line;

		line=myVector1.getLinetext(fieldSep, valueSep);

		System.out.println(" vector1 :" + line);

		SmallItem myVector2= new SmallItem();
		myVector2.initViaLineIndexValue(line, fieldSep, valueSep);

		line=myVector2.getLinetext(fieldSep, valueSep);
		System.out.println(" vector2 (copie 1 via String) :" + line);
		SmallItem myVector3= new SmallItem();
		myVector3=myVector2.getClone();
		myVector2=new SmallItem();
		line=myVector2.getLinetext(fieldSep, valueSep);
		System.out.println(" vector2 (effacé) :" + line);
		line=myVector3.getLinetext(fieldSep, valueSep);
		System.out.println(" vector3 (clone vector2 avant effacement) :" + line);
		System.out.println();
		System.out.println("----  generation aléatoire ------");
		//System.out.println();
		SmallItem myVector4=new SmallItem();
		myVector4.generateRandomBinaryVector(20, 0.3);
		line=myVector4.getLinetext(fieldSep, valueSep);


		System.out.println(" vector4 alea :" + line);

		System.out.println();
		System.out.println("---------- cosinus -------------");
		
		String recordA="x1=1 ; x2=0";
		String recordB="x1=1 ; x2=1";
		String recordC="x1=1 ; x2=-1";
		
		SmallItem myVectorA=new SmallItem();
		SmallItem myVectorB=new SmallItem();
		SmallItem myVectorC=new SmallItem();
		
		myVectorA.initViaLineIndexValue(recordA, fieldSep, valueSep);
		myVectorB.initViaLineIndexValue(recordB, fieldSep, valueSep);
		myVectorC.initViaLineIndexValue(recordC, fieldSep, valueSep);
		String lineA, lineB,lineC;
		lineA=myVectorA.getLinetext(fieldSep, valueSep);
		lineB=myVectorB.getLinetext(fieldSep, valueSep);
		lineC=myVectorC.getLinetext(fieldSep, valueSep);
		System.out.println("vector A : "+lineA);
		System.out.println("vector B : "+lineB);
		System.out.println("vector C : "+lineC);
		
		System.out.println("cos(A, B)="+SmallItem.getSparseVectorCosine(myVectorA, myVectorB));
		System.out.println("cos(A, C)="+SmallItem.getSparseVectorCosine(myVectorA, myVectorC));
		System.out.println("cos(B, C)="+SmallItem.getSparseVectorCosine(myVectorB, myVectorC));
		System.out.println("cos(A, A)="+SmallItem.getSparseVectorCosine(myVectorA, myVectorA));
		System.out.println();
		System.out.println("---------------- Test delete Keys Ref -------------");
		 lineA=" a=1; b=1; c=3 ; d=4 ; e=5; ";
		 lineB=" a=1 ; d=2";
		SmallItem a = new SmallItem();
		a.initViaLineIndexValue(lineA, ";", "=");
		SmallItem b = new SmallItem();
		b.initViaLineIndexValue(lineB, ";", "=");
		System.out.println("a:"+a.getLinetext(";", "="));
		System.out.println("b:"+b.getLinetext(";", "="));
		System.out.println("a delete from B :");
		a.deleteReferenceKeys(b);
		System.out.println(a.getLinetext(";", "="));

		
	}


}




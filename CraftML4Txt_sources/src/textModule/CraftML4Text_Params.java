package textModule;

public class CraftML4Text_Params {
	
	static public final String infoVersion="CraftML for Text v0.97 - 2020/03/12 ";
	// ========================= Parsing Parameters ===================================
	
	public String commentarySep="#";
	public String xySep="=>";       // separator between the raw text (X part) and the list of labels (Y part)
	public String[] labelSeparator= {";" ,  " " ,  "\t" , ","  };  // separators for the list of labels (Y part) : by default blank, comma, tabulation and semicolon
	public String equalSeparator="=";  // equal symbol, used for the predicted labels in the form of: labe14=0.95 ; label32=0.67 ; label9=0.60  
	public String predictionWriterPrefix="\t//\t";  // separator added after the commentary separator and before the prediction ; can be used to better extract prediction afterwards
	public String fieldSeparator=";";  // separator used for "label=value" descriptions (for both text prediction and save/load model)
	
	public int maxCharNgram=4;  // size max for ngram of characters, for text parsing, 0 for no letter ngram
	public int maxWordNgram=4;  // size max for ngrams of words, for text parsing, 0 is forbidden (put 1)
	
	public boolean removePunctuation=true; // remove punctuation true/false in the x text
	public boolean caseSentitive=false ; // if false, the x text is always converted into lowerCase
	
	//===  div parameters
	public boolean addClusterInfoPrediction=true;
	
	//===   eval metric parameters
	//public int precisionAtK=3;  // default is 3
	
	//============================= CraftML model's parameters ==========================
	
	public int numberOfTrees=10; // by default;
	public int branchFactor=10; // by default
	public int topNLabels=3; // by default : number of labels predicted
	public int minInstanceInLeaf=5; // minimum number of examples in a leaf to continue to split
	public int depthMax=10; 
	public int sizeReservoirSampling4Kmeans=20000 ; // number of examples kept to carry out the kmeans at each step
	public int xProjectionSize=10000;   // size of projection for text's features   (X part)
	public int yProjectionSize=1000;   // size of projection for label's features  (Y part)
	public int sparsity=1000;          // number of projections' features (X space) kept after the spherical kmeans clustering and the projection (top sparsity of weights)
	// --- not used for the moment 
	public int nbThread=4; // by default
	public boolean optimiseMemory=false;
	public boolean allTreesTogether=false;
	
	
	
	public String loadParameters(String globalPath) {  //TODO
		return null; // TODO
	}
	
	public String saveParameters(String globalPath) {
		return null; // TODO
	}

}

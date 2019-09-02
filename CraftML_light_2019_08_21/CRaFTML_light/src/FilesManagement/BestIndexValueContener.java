package FilesManagement;

import Algorithm.Displayer;

public class BestIndexValueContener {

	
	public int[] bestIndex;
	public float[] value;
	public String[] keys;
	
	
	public static void error(String mess) {
		Displayer.displayText("Error:\n" + mess);
		Error er = new Error(mess);
		throw er;
	}
	
	public BestIndexValueContener(int[] indexes, float[] values) {
		if (indexes.length!=values.length) {
			System.out.println("index tab size:"+indexes.length);
			System.out.println("value tab size:"+values.length);
			error("size index and value not compatible");
		}
		bestIndex=indexes ; 
		value=values;
	}
	
	public BestIndexValueContener(int[] indexes, float[] values,String[] dico) {
		if (indexes.length!=values.length) {
			System.out.println("index tab size:"+indexes.length);
			System.out.println("value tab size:"+values.length);
			error("size index and value not compatible");
		}
		bestIndex=indexes ; 
		value=values;
		keys = new String[values.length];
		for(int i = 0; i<values.length;i++){
			keys[i] = dico[indexes[i]];
		}
	}
	
}

package Algorithm;

public class Displayer {
	
	public static boolean onTrace = true;
	public static boolean onTraceDebug = true;
	public static String decimalFormat = "0.0000000000";
	
	public static void displayText(String text){
		if(onTrace){
			System.out.println(text);
		}
	}
	
	public static void displayDebug(String text){
		if(onTraceDebug){
			System.out.println(text);
		}
	}
	
public static String floatArrayToString(float[] vector,String Separateur) {
		
		String output = "";
		for(int i=0; i<vector.length - 1; i++){				
			output = output + Float.toString(vector[i]) + Separateur;
		}
		output = output + Float.toString(vector[vector.length - 1]);
		return output;
	}
	
	
	public static void displayFloatArray(float[] vector,String Separateur){
		if(onTrace){
			if(Separateur =="" || Separateur == null){
				Separateur = "  ";
			}
			java.text.DecimalFormat df = new java.text.DecimalFormat(decimalFormat);
			for(int i=0; i<vector.length; i++){				
				System.out.print(df.format(vector[i])+ Separateur);
			}
			System.out.println("\n");
		}
	}

}

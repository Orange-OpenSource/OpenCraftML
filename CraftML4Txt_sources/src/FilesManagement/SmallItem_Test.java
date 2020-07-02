package FilesManagement;

public class SmallItem_Test {

	public static void main(String[] arguments) {
		
		
		SmallItem si= new SmallItem();
		String values=" a=2 ; b=3.1 ; c=-1.8 ; d=9; e=2.4 ; f=+6.4 ";
		si.initViaLineIndexValue(values, ";", "=");
		System.out.println("si: "+si.getLinetext(";", "="));
		
		System.out.println("TOP 3:");
		String[] top3keys=si.getBestKeysDecreasingOrder(3);
		System.out.println(si.getOrderedLinetext(";", "=", top3keys));
		
	}
	
	
}

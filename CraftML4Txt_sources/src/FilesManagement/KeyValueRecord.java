package FilesManagement;

import java.util.Arrays;

public class KeyValueRecord implements Comparable <KeyValueRecord>{

	//class Item implements Comparable<Item> {
		
		public String key;
		public float value;
		
		public KeyValueRecord(String key, float value) {
			this.key=key;
			this.value=value;
		}
		
		@Override
		public int compareTo(KeyValueRecord other) {
			return (this.value < other.value) ? 1 : ((this.value == other.value) ? 0 : -1); // decreasing order
		}
		
		
		
		public static void main( String[] argv) {
			
			KeyValueRecord[] myRecords= new KeyValueRecord[5];
			for (int i=0; i<5;i++) {
				KeyValueRecord kv= new KeyValueRecord("ID"+i, i*10);
				myRecords[i]=kv;
			}
			
			Arrays.sort(myRecords);
			
			//Arrays.asList(myRecords).forEach(item -> System.out.println(item.value)); // 5 4 3 2 1
		}
	//}
	
	
}

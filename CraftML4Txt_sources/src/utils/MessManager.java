package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessManager {

	long myTimeStamp=0;
	
	public static synchronized void say(String mess) {
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		//long t=System.currentTimeMillis();
		//System.out.println("T: "+t);
		System.out.println("CraftML: "+mess );
	}
	
	public static synchronized void sayError(String mess) {
		say("ERROR: "+mess);
		Error e=new Error();
		e.printStackTrace();
		System.exit(1);
	}
	
	
	public static synchronized void sayWarning(String mess) {
		say("WARNING :"+mess);
	}
	
	
}

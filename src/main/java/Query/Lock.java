package Query;

import java.util.HashSet;
import java.util.Set;

public class Lock {
	public static Set<String> tableSet = new HashSet <String>();
	
	public static void addLock(String tablename) {
		tableSet.add(tablename);
		System.out.println("locked table: "+ tableSet);
	}
	
	public static boolean checkLock(String tablename) {
		System.out.println("lock tables: "+ tableSet);
		boolean status = false;
		if(tableSet.contains(tablename)) {
			status = true;
		}
		return status;
	}
	
	public static void removeLock(String tablename) {
		tableSet.remove(tablename);
		System.out.println("removed lock: "+tableSet);
	}
}

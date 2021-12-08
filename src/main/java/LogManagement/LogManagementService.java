package LogManagement;

import java.util.Map;

public class LogManagementService {
	
	public static final String EXECUTION_TIME_KEY = "QUERY_EXECUTION_TIME";
	public static final String DB_STATE_KEY = "DB_KEY";
	
	public static final String QUERY_EXECUTED_KEY = "QUERY_EXECUTED";
	
	public static final String DB_CHANGE_KEY = "CHANGE_IN_DB";
	public static final String CONCURRENT_TRANSACTIONS_KEY = "CONCURRENT_TRANS_KEY";
	public static final String DB_CRASH_KEY = "DB_CRASH";
	
	private static LogManagementService instance = null;
	
	public static LogManagementService getInstance() {
		if(instance==null) {
			instance = new LogManagementService();
		}
		return instance;
	}
	
	private LogManagementService() {
		begin();
	}
	
	public void begin() {
		GeneralLog.getInstance().begin();
		EventLog.getInstance().begin();
		QueryLog.getInstance().begin();
	}
	
	public void writeLog(Map<String,String> infoMap) {
		if(infoMap.containsKey(EXECUTION_TIME_KEY) && infoMap.containsKey(DB_STATE_KEY)) {
			GeneralLog.getInstance().generalWriteLog(infoMap);
		}
		if(infoMap.containsKey(QUERY_EXECUTED_KEY)) {
			QueryLog.getInstance().queryWriteLog(infoMap);
		}
		if(infoMap.containsKey(CONCURRENT_TRANSACTIONS_KEY) || infoMap.containsKey(DB_CRASH_KEY) || infoMap.containsKey(DB_CHANGE_KEY)) {
			EventLog.getInstance().eventWriteLog(infoMap);
		}
	}
	
	public void finish() {
		GeneralLog.getInstance().finish();
		QueryLog.getInstance().finish();
		EventLog.getInstance().finish();
	}
}

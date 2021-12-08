package LogManagement;

import java.util.List;
import java.util.Map;

public class LogManagementWriter {
	public static enum TYPE_OF_LOG {
		QUERY_LOG, EVENT_LOG, GENERAL_LOG;
	}
	
	private static LogManagementWriter instance = null;
	
	public static LogManagementWriter getInstance() {
		if(instance == null) {
			instance = new LogManagementWriter();
		}
		return instance;
	}
	
	private LogManagementWriter() {
	}
	
	public void writeLog(Map<String, String> queryInformation, TYPE_OF_LOG logType) {
		if(logType == TYPE_OF_LOG.QUERY_LOG) {
			QueryLog.getInstance().queryWriteLog(queryInformation);
		}
		else if(logType == TYPE_OF_LOG.GENERAL_LOG) {
			GeneralLog.getInstance().generalWriteLog(queryInformation);
		}
		else if(logType == TYPE_OF_LOG.EVENT_LOG) {
			EventLog.getInstance().eventWriteLog(queryInformation);
		}
	}
}

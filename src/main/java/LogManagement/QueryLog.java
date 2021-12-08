package LogManagement;

import Authentication.UserModel;
import Constants.fileLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class QueryLog {

	private String queryLogFilePath = fileLocation.LOCAL_PATH + "/Query_Logs.txt";
	
	private File file;
	private java.io.FileWriter queryFileWriter;
	private BufferedWriter bw;
	
	private static QueryLog instance = null;
	
	public static QueryLog getInstance() {
		if(instance==null) {
			instance = new QueryLog();
		}
		return instance;
	}
	
	private QueryLog() {
	}
	
	public void begin() {
		try {
			file = new File(this.queryLogFilePath);
			file.createNewFile();
			this.queryFileWriter = new java.io.FileWriter(this.file, true);
			this.bw = new BufferedWriter(this.queryFileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean queryWriteLog(Map<String, String> infoMap) {
		try {
			Calendar calendar = Calendar.getInstance();
			StringBuffer sb = new StringBuffer();
			sb.append("<").append(calendar.getTime()).append(">");
			sb.append("<").append(UserModel.getinstance().getUsername()).append(">");
			sb.append("<").append("User Query: ").append(infoMap.get(LogManagementService.QUERY_EXECUTED_KEY)).append(">");
			this.bw.newLine();
			this.bw.append(sb.toString());
			this.bw.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void finish() {
		try {
			this.bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

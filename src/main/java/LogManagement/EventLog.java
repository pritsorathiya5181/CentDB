package LogManagement;

import Authentication.UserModel;
import Constants.fileLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class EventLog {
    //	private String eventLogsFilePath = "/Users/vibhorbhatnagar/Desktop/Event_Logs.txt";
    private String eventLogsFilePath = fileLocation.LOCAL_PATH + "/Event_Logs.txt";

    private File file;
    private java.io.FileWriter eventLogFileWriter;
    private BufferedWriter bw;

    private static EventLog instance = null;

    public static EventLog getInstance() {
        if (instance == null) {
            instance = new EventLog();
        }
        return instance;
    }

    private EventLog() {
    }

    public void begin() {
        try {
            file = new File(this.eventLogsFilePath);
            file.createNewFile();
            this.eventLogFileWriter = new java.io.FileWriter(this.file, true);
            this.bw = new BufferedWriter(this.eventLogFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean eventWriteLog(Map<String, String> eventInformationMap) {
        try {
            Calendar calendar = Calendar.getInstance();
            StringBuffer sb = new StringBuffer();
            sb.append("{").append(calendar.getTime()).append("}");
            sb.append("{").append(UserModel.getinstance().getUsername()).append("}");
            sb.append("{");
            if (eventInformationMap.containsKey(LogManagementService.DB_CHANGE_KEY)) {
                sb.append("Database changes are: ").append(eventInformationMap.get(LogManagementService.DB_CHANGE_KEY));
            }
            if (eventInformationMap.containsKey(LogManagementService.CONCURRENT_TRANSACTIONS_KEY)) {
                sb.append("Active concurrent transactions are: ").append(eventInformationMap.get(LogManagementService.CONCURRENT_TRANSACTIONS_KEY));
            }
            if (eventInformationMap.containsKey(LogManagementService.DB_CRASH_KEY)) {
                sb.append("Database crashed due to: ").append(eventInformationMap.get(LogManagementService.DB_CRASH_KEY));
            }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

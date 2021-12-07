package LogManagement;

import Authentication.UserModel;
import Constants.fileLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class GeneralLog {
    //	private String generalLogsFilePath = "/Users/vibhorbhatnagar/Desktop/General_Logs.txt";
    private String generalLogsFilePath = fileLocation.LOCAL_PATH + "/Event_Logs.txt";

    private File file;
    private java.io.FileWriter generalLogFileWriter;
    private BufferedWriter bw;

    private static GeneralLog instance = null;

    public static GeneralLog getInstance() {
        if (instance == null) {
            instance = new GeneralLog();
        }
        return instance;
    }

    private GeneralLog() {
    }

    public void begin() {
        try {
            file = new File(this.generalLogsFilePath);
            file.createNewFile();
            this.generalLogFileWriter = new java.io.FileWriter(this.file, true);
            this.bw = new BufferedWriter(this.generalLogFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean generalWriteLog(Map<String, String> generalInformationMap) {
        try {
            Calendar calendar = Calendar.getInstance();
            StringBuffer sb = new StringBuffer();
            sb.append("{").append(calendar.getTime()).append("}");
            sb.append("{").append(UserModel.getinstance().getUsername()).append("}");
            sb.append("{").append("Execution time for this query: ")
                    .append(generalInformationMap.get(LogManagementService.EXECUTION_TIME_KEY)).append(". DB State is: ")
                    .append(generalInformationMap.get(LogManagementService.DB_STATE_KEY)).append("}");
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

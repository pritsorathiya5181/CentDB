package Query;

import Constants.fileLocation;

import java.io.File;

public class DatabaseOperation {
    public String currentDatabase;

    public String getCurrentDatabase() {
        return currentDatabase;
    }

    public void setCurrentDatabase(String currentDatabase) {
        this.currentDatabase = currentDatabase;
    }

    public boolean createDb(String dbName) {
        boolean status = false;
        File dbFolder = new File(fileLocation.LOCAL_PATH+"/"+dbName);
        if(!dbFolder.exists()) {
            status = dbFolder.mkdir();
        }
        System.out.println(dbName+" database is already exists");
        return status;
    }

    public void useDb(String dbName){
        setCurrentDatabase(dbName);
    }
}

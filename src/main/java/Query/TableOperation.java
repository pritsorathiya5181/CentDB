package Query;

import Constants.fileLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableOperation {
    public boolean createTable(String dbName, String tableName, ArrayList<String> colName, ArrayList<String> colValue, HashMap<String, String> keySet) {
        File dictonaryFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/dataDictionary.txt");
        try {
            if (dictonaryFile.createNewFile()) {
                System.out.println("Create new file with table name: " + tableName + " in database: " + dbName);
            }
            FileWriter fileWriter = new FileWriter(dictonaryFile, true);

            fileWriter.append(tableName);
            fileWriter.append(" ");
            for (int i = 0; i < colName.size(); i++) {
                fileWriter.append(colName.get(i).strip());
                fileWriter.append(" ");
                fileWriter.append(colValue.get(i).strip());
                fileWriter.append(" ");
                if (keySet.containsKey(colName.get(i))) {
                    fileWriter.append((keySet.get(colName.get(i))));
                    fileWriter.append(" ");
                }
//                fileWriter.append("\n");
            }
            fileWriter.append("\n");
            fileWriter.close();

            File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
            tableFile.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(String dbName, String tableName, ArrayList<String> columns, ArrayList<String> values) {

        ArrayList<String> pkColValues = new ArrayList<>();
        String tableFilePath = fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt";
        File tableFile = new File(tableFilePath);

        if (!tableFile.exists()) {
            System.out.println(tableName + " table doesn't exist");
            return false;
        }
        try {
            Scanner myReader = new Scanner(tableFile);
            String primaryKey = getPrimaryKeyColumn(dbName, tableName);

            if(primaryKey == null) {
                System.out.println("No primary key in table");
            }
            else {
                Map<String, ArrayList<String>> records = getRecords(myReader);
                pkColValues = getColumnValues(primaryKey, records);
            }

            FileWriter fileWriter = new FileWriter(tableFile, true);

            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).equals(primaryKey) && pkColValues != null){
                    if (pkColValues.contains(values.get(i).strip())) {
                        System.out.println("Cannot allow to insert duplicate value in PK");
                        return false;
                    }
                    Pattern p = Pattern.compile("\'([^\"]*)\'");
                    Matcher m = p.matcher(values.get(i).strip());
                    String colValue = m.find() ? m.group(1) : values.get(i).strip();
                    if(colValue.equals("")) {
                        System.out.println("Primary Key should be not null");
                        return false;
                    }
                }
                fileWriter.append(columns.get(i).strip());
                fileWriter.append(" ");
                fileWriter.append(values.get(i).strip());
                fileWriter.append("\n");
            }
            fileWriter.append("\n");
            fileWriter.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<String> getColumnValues(String columnName, Map<String, ArrayList<String>> records) {
        for (Map.Entry<String, ArrayList<String>> entry : records.entrySet()) {
            if (columnName.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Map<String, ArrayList<String>> getRecords(Scanner myReader) {
        Map<String, ArrayList<String>> records = new HashMap<>();
        ArrayList<String> temp;

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (data.strip().length() > 0) {
                String[] entries = data.split(" ");
                if (records.containsKey(entries[0])) {
                    temp = new ArrayList<>(records.get(entries[0]));
                } else {
                    temp = new ArrayList<>();
                }
//                temp.add(entries[1]);
                temp.add(data.substring(data.indexOf(entries[0]) + entries[0].length()).strip());
                records.put(entries[0], temp);
            }
        }
        return records;
    }

    private String getPrimaryKeyColumn(String dbName, String tableName) {
        try {
            String dictionaryPath = fileLocation.LOCAL_PATH + "/" + dbName + "/dataDictionary.txt";
            File dataDict = new File(dictionaryPath);
            Scanner myReader = new Scanner(dataDict);

            while (myReader.hasNextLine()) {
                String tableDef = myReader.nextLine();
                if (tableDef.contains(tableName)) {
                    if (tableDef.contains("PK")) {
                        String[] tableStr = tableDef.split(" ");
                        int pkPos = Arrays.stream(tableStr).toList().indexOf("PK");
                        return tableStr[pkPos - 2];
                    }
                }
            }
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }
}

//    CREATE TABLE Customers (CustomerName int, ContactName varchar(255), Address varchar(255), City varchar(255), PostalCode varchar(255), Country varchar(255));
//
//        INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', '4006', 'Norway');
//INSERT INTO Person (pName, pId, pAdd) VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21');
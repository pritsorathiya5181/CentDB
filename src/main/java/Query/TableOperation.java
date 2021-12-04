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
        File dictionaryFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/dataDictionary.txt");
        try {
            if (dictionaryFile.createNewFile()) {
                System.out.println("Create new file with table name: " + tableName + " in database: " + dbName);
            }
            FileWriter fileWriter = new FileWriter(dictionaryFile, true);

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

            if (primaryKey == null) {
                System.out.println("No primary key in table");
            } else {
                Map<String, ArrayList<String>> records = getRecords(myReader);
                pkColValues = getColumnValues(primaryKey, records);
            }

            FileWriter fileWriter = new FileWriter(tableFile, true);

            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).equals(primaryKey) && pkColValues != null) {
                    if (pkColValues.contains(values.get(i).strip())) {
                        System.out.println("Cannot allow to insert duplicate value in PK");
                        return false;
                    }
                    Pattern p = Pattern.compile("\'([^\"]*)\'");
                    Matcher m = p.matcher(values.get(i).strip());
                    String colValue = m.find() ? m.group(1) : values.get(i).strip();
                    if (colValue.equals("")) {
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

    public boolean select(String dbName, String tableName, ArrayList<String> columns, String conditionColumns, String conditionValues) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        if (!tableFile.exists()) {
            System.out.println(tableName + " table doesn't exist");
            return false;
        }
        try {
            Scanner myReader = new Scanner(tableFile);
            Map<String, ArrayList<String>> records = getRecords(myReader);
            ArrayList<String> temp;
            ArrayList<Integer> presentIn = new ArrayList<>();
            if (conditionColumns != null) {
                ArrayList<String> col = records.get(conditionColumns);
                for (int i = 0; i < col.size(); i++) {
                    if (col.get(i).equals(conditionValues)) {
                        presentIn.add(i);
                    }
                }
                System.out.println(presentIn.toString());
            }

            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                String columnName = ee.getKey();
                if (columns.contains(columnName) || columns.contains("*")) {
                    System.out.printf("%-15s=>", columnName);
                    temp = ee.getValue();
                    for (int i = 0; i < temp.size(); i++) {
                        if (conditionColumns != null && presentIn.contains(i))
                            System.out.printf("%-20s", temp.get(i));
                        else if (conditionColumns == null)
                            System.out.printf("%-20s", temp.get(i));
                    }
                    System.out.println("\n");
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(String dbName, String tableName, ArrayList<String> columns, ArrayList<String> values, String conditionColumns, String clmValues) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        String conditionValues = "'" + clmValues + "'";
        if (!tableFile.exists()) {
            System.out.println(tableName + " table doesn't exist");
            return false;
        }
        try {
            Scanner myReader = new Scanner(tableFile);
            Map<String, ArrayList<String>> records = getRecords(myReader);
            ArrayList<Integer> presentIn = new ArrayList<>();
            ArrayList<String> temp;

            if (conditionColumns != null) {
                ArrayList<String> col = records.get(conditionColumns);
                for (int i = 0; i < col.size(); i++) {
                    String colVal = col.get(i).replace("\"", "");
                    if (colVal.equals(conditionValues)) {
                        presentIn.add(i);
                    }
                }
            }
//            UPDATE Customers SET City=Halifax WHERE Country=Canada;
            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                temp = ee.getValue();
                for (int i = 0; i < temp.size(); i++) {
                    if (columns.contains(ee.getKey()) && presentIn.contains(i)) {
                        int index = columns.indexOf(ee.getKey());
                        temp.set(i, "'" + values.get(index) + "'");
                        records.put(ee.getKey(), temp);
                    }
                }
            }

            FileWriter writer = new FileWriter(tableFile, false);
            for (int i = 0; i < records.get(conditionColumns).size(); i++) {
                for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                    String record = ee.getKey() + " " + ee.getValue().get(i) + "\n";
                    writer.write(record);
                    writer.flush();
                }
                writer.write("\n");
                writer.flush();
            }
            writer.close();
            System.out.println("value Updated successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean truncate(String dbName, String tableName) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        if (!tableFile.exists()) {
            System.out.println("Table Doesn't exist");
            return false;
        }

        try {
            FileWriter writer = new FileWriter(tableFile, false);
            System.out.println("Table Truncated successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean drop(String dbName, String tableName) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        if (!tableFile.exists()) {
            System.out.println("Table Doesn't exist");
            return false;
        } else {
            boolean deleteStatus = tableFile.delete();

        }
        File dataDictionaryFile = new File(fileLocation.LOCAL_PATH  + "/" + dbName + "/" + "dataDictionary.txt");
        try {
            Scanner myReader = new Scanner(dataDictionaryFile);
            Map<String, ArrayList<String>> records = getRecords(myReader);

            FileWriter writer = new FileWriter(dataDictionaryFile, false);
            ArrayList<String> temp;

            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                StringBuilder record = new StringBuilder();
                if (!ee.getKey().equals(tableName)) {
                    temp = ee.getValue();
                    for (String s : temp) {
                        record.append(s).append("\n");
                    }
                    writer.write(record.toString());
                    writer.flush();
                    writer.write("\n");
                    writer.flush();
                }
            }
            System.out.println("table Dropped successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}

//   USE <DATABASE NAME>
//   CREATE TABLE Customers (CustomerName int,ContactName varchar(255),Address varchar(255),City varchar(255),PostalCode varchar(255),Country varchar(255));
//   SELECT * FROM Customers;
//   INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', '4006', 'Norway');
//   INSERT INTO Person (pName, pId, pAdd) VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21');
//   UPDATE Customers SET Country=India WHERE CustomerName=Cardinal;
//   TRUNCATE TABLE Customers;
//   DROP TABLE Customers;
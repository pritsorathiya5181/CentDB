package Query;

import Constants.*;

import java.io.*;
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

            File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
            if (tableFile.createNewFile()) {
                fileWriter.append(tableName);
                fileWriter.append("\n");
                for (int i = 0; i < colName.size(); i++) {
                    fileWriter.append(colName.get(i).strip());
                    fileWriter.append(" ");
                    fileWriter.append(colValue.get(i).strip());
                    fileWriter.append(" ");
                    if (keySet.containsKey(colName.get(i))) {
                        fileWriter.append((keySet.get(colName.get(i))));
                        fileWriter.append(" ");
                    }
                    fileWriter.append("\n");
                }
                fileWriter.append("\n\n");
                fileWriter.close();
                return true;
            } else {
                System.out.println(tableName + " table is already exist");
                return false;
            }
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
                System.out.println("No primary key is in table");
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
//                fileWriter.append(values.get(i).strip().replaceAll("^\'|\'$", ""));
                fileWriter.append(values.get(i));
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
                    tableDef = myReader.nextLine();
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

    public int select(String dbName, String tableName, ArrayList<String> columns, String conditionColumns, String clmValues) {
        String conditionValues = "'" + clmValues + "'";
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        if (!tableFile.exists()) {
            System.out.println(tableName + " table doesn't exist");
            return 0;
        }
        try {
            Scanner myReader = new Scanner(tableFile);
            Map<String, ArrayList<String>> records = getRecords(myReader);
            ArrayList<String> temp;
            ArrayList<Integer> presentIn = new ArrayList<>();
            if (conditionColumns != null) {
                ArrayList<String> col = records.get(conditionColumns);
                for (int i = 0; i < col.size(); i++) {
                    if (col.get(i).equals(conditionValues) || col.get(i).equals(clmValues)) {
                        presentIn.add(i);
                    }
                }
                System.out.println(presentIn.toString());
            }
            int countWhenHasCondition = 0;
            int countWhenNotHaveCondition = 0;

            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                String columnName = ee.getKey();
                if (columns.contains(columnName) || columns.contains("*")) {
                    System.out.printf("%-15s=>", columnName);
                    temp = ee.getValue();
                    for (int i = 0; i < temp.size(); i++) {
                        if (conditionColumns != null && presentIn.contains(i)) {
                            System.out.printf("%-20s", temp.get(i));
                            countWhenHasCondition = temp.size();
                        } else if (conditionColumns == null) {
                            System.out.printf("%-20s", temp.get(i));
                            countWhenNotHaveCondition = temp.size();
                        }
                    }
                    System.out.println("\n");
                }
            }
            if (conditionColumns != null) {
                return countWhenHasCondition;
            } else {
                return countWhenNotHaveCondition;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int update(String dbName, String tableName, ArrayList<String> columns, ArrayList<String> values, String conditionColumns, String clmValues) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        String conditionValues = "'" + clmValues + "'";
        if (!tableFile.exists()) {
            System.out.println(tableName + " table doesn't exist");
            return 0;
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
                    if (colVal.equals(conditionValues) || colVal.equals(clmValues)) {
                        presentIn.add(i);
                    }
                }
            }
            int count = 0;
//            UPDATE Customers SET City=Halifax WHERE Country=Canada;
            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                temp = ee.getValue();
                for (int i = 0; i < temp.size(); i++) {
                    if (columns.contains(ee.getKey()) && presentIn.contains(i)) {
                        int index = columns.indexOf(ee.getKey());
                        temp.set(i, "'" +values.get(index) +"'");
                        records.put(ee.getKey(), temp);
                        count++;
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
            return count;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean delete(String dbName, String tableName, String conditionColumns, String clmValues) {
        File tableFile = new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + tableName + ".txt");
        String conditionValues = "'" + clmValues + "'";

        if (!tableFile.exists()) {
            System.out.println("Table Doesn't exist");
            return false;
        }
        try {
            Scanner myReader = new Scanner(tableFile);
            Map<String, ArrayList<String>> records = getRecords(myReader);

            FileWriter writer = new FileWriter(tableFile, false);
            ArrayList<String> temp;
            System.out.println("records==" + records + "en==" + records.keySet());

            Set<String> columnList = records.keySet();
//            System.out.println(columnList.toArray()[0]);
            for (int i = 0; i < records.get(columnList.toArray()[0]).size(); i++) {
                String record = "";
                for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                    boolean hasKey = ee.getKey().equals(conditionColumns);
                    boolean hasValue = ee.getValue().get(i).equals(conditionValues) || ee.getValue().get(i).equals(clmValues);

                    if (!(hasKey && hasValue)) {
                        record += ee.getKey() + " " + ee.getValue().get(i) + "\n";
                    } else {
                        record = "";
                        break;
                    }
                }
                writer.write(record);
                writer.flush();
                writer.write("\n");
                writer.flush();
            }

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
            tableFile.delete();
        }

        File dataDictionaryFile =
                new File(fileLocation.LOCAL_PATH + "/" + dbName + "/" + "dataDictionary.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dataDictionaryFile));
            String st, tableKey = null;
            HashMap<String, ArrayList<String>> records = new HashMap<>();
            ArrayList<String> temp = null;
            while ((st = reader.readLine()) != null) {
                if (st.length() > 0) {
                    String[] array = st.trim().split(" ");
                    if (array.length == 1) {
                        tableKey = st;
                    }
                    if (records.containsKey(tableKey)) {
                        temp = new ArrayList<>(records.get(tableKey));
                    } else {
                        temp = new ArrayList<>();
                    }
                    temp.add(st);
                }
                records.put(tableKey, temp);
            }

            FileWriter writer = null;
            writer = new FileWriter(dataDictionaryFile, false);
            for (Map.Entry<String, ArrayList<String>> ee : records.entrySet()) {
                String record = "";
                if (!ee.getKey().equals(tableName)) {
                    temp = ee.getValue();
                    for (int i = 0; i < temp.size(); i++) {
                        record += temp.get(i) + "\n";
                    }
                    writer.write(record);
                    writer.flush();
                    writer.write("\n");
                    writer.flush();
                }
            }
            System.out.println("table Dropped successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
//   CREATE TABLE student_information (student_id int PK,first_name char FK,last_name char,contact_number char REFERENCES grade student_id);
//   INSERT INTO student_information (student_id, first_name, contact_number) VALUES ('b0090100', 'Mark', '902985275');
//   DELETE FROM Customers WHERE CustomerName=Cardinal;
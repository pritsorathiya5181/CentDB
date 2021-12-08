package erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.fileLocation;

public class ExportERD {

    private String databaseName;
    private String databasePath = fileLocation.LOCAL_PATH + "/";
    private Map<String, Map<String, String>> databaseTableDataTypeMap;
    private Map<String, Map<String, String>> databaseTableColumnConstraintMap;
    private List<String> databaseTables;
    private final static String TABLE_FORMAT_STRING = "%2s %25s %2s %25s %2s %25s %2s";

    /**
     *
     * @param databaseName
     */
    public ExportERD(String databaseName) {
        this.databaseName = databaseName;
        databasePath = databasePath + databaseName;
        this.databaseTableDataTypeMap = new HashMap<>();
        this.databaseTables = new ArrayList<>();
        this.databaseTableColumnConstraintMap = new HashMap<>();
    }

    public void executeGenerateERD() {
        System.out.println("Generating ERD Table for " + databaseName + "! Please wait a while...");
        extractTableMetadata();
        System.out.println(this.toString());
        generateExportFile();
    }

    /**
     * method to generate table creation of tables
     *
     * @return
     */
    private void extractTableMetadata() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(this.databasePath + "/dataDictionary.txt"))) {
            Thread.sleep(1000);
            String tableName = null;
            Map<String, String> tableDataTypesMap = new HashMap<>();
            Map<String, String> columnConstraintMap = new HashMap<>();
            for (String line; (line = bufferedReader.readLine()) != null;) {
                String[] parts = line.split("\\s+");
                if (line.isEmpty() && tableDataTypesMap.size() != 0 && tableName != null) {
                    this.databaseTableDataTypeMap.put(tableName, tableDataTypesMap);
                    this.databaseTableColumnConstraintMap.put(tableName, columnConstraintMap);
                    tableName = null;
                    columnConstraintMap = new HashMap<>();
                    tableDataTypesMap = new HashMap<>();
                    bufferedReader.readLine();
                } else if (!line.isEmpty() && parts.length == 1) {
                    databaseTables.add(parts[0]);
                    tableName = parts[0];
                } else if (parts.length == 2) {
                    tableDataTypesMap.put(parts[0], parts[1]);
                } else if (parts.length == 3) {
                    columnConstraintMap.put(parts[0], "PRIMARY KEY");
                    tableDataTypesMap.put(parts[0], parts[1]);
                } else if (parts.length == 5) {
                    columnConstraintMap.put(parts[0], "FOREIGN KEY");
                    tableDataTypesMap.put(parts[0], parts[1]);
                } else if (parts.length == 6) {
                    columnConstraintMap.put(parts[0], "FOREIGN KEY REFERENCES " + parts[4] + "(" + parts[5] + ")");
                    tableDataTypesMap.put(parts[0], parts[1]);
                }
            }
            // for (Map.Entry<String, String> entry : databaseTables.entrySet()) {
            // System.out.println(entry.getKey() + " -> " + entry.getValue());
            // }
        } catch (NoSuchFileException | FileNotFoundException e) {
            System.out.println("No database found with given name.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while fetching database. Please try again.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * method to generate *.erd.txt file
     */
    private void generateExportFile() {
        File sqlFile = new File(this.databasePath + "/" + this.databaseName + ".erd.txt");
        try (FileWriter fileWriter = new FileWriter(sqlFile);) {
            fileWriter.write(this.toString());
            System.out.println("ERD exported successfully.");
            System.out.println("Export Location: " + this.databasePath + "/" + this.databaseName + ".erd.txt");
        } catch (IOException e) {
            System.out.println("Error occurred while generating .sql file. Please try again.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sqlExportStringBuilder = new StringBuilder();
        sqlExportStringBuilder.append("\n Database: " + this.databaseName + "\n\n");
        for (String tableName : databaseTables) {
            sqlExportStringBuilder.append(String.format("%2s %10s", "|", " Table: " + tableName) + "\n");
            sqlExportStringBuilder
                    .append(String.format(TABLE_FORMAT_STRING, "|", "Column", "|", "Data Type", "|",
                            "Constraints", "|") + "\n");
            sqlExportStringBuilder
                    .append(String.format(TABLE_FORMAT_STRING, "--", "--------------------------", "--",
                            "--------------------------", "--", "--------------------------", "") + "\n");
            for (Map.Entry<String, String> entry : this.databaseTableDataTypeMap.get(tableName).entrySet()) {
                String constraints = this.databaseTableColumnConstraintMap.get(tableName).get(entry.getKey());
                sqlExportStringBuilder
                        .append(String.format(TABLE_FORMAT_STRING, "|", entry.getKey(), "|",
                                entry.getValue(), "|", constraints == null ? "-" : constraints, "|") + "\n");
            }
            sqlExportStringBuilder
                    .append(String.format(TABLE_FORMAT_STRING, "--", "--------------------------", "--",
                            "--------------------------", "--", "--------------------------", "") + "\n\n");

        }
        sqlExportStringBuilder
                .append("--  ERD generated on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return sqlExportStringBuilder.toString();
    }

    private int identifyDataTypeReturnInt(String item) {
        return item.contains("int") ? 0 : 1;
    }

    private String identifyDataType(String item) {
        return item.contains("int") ? " int" : " varchar(255)";
    }
}

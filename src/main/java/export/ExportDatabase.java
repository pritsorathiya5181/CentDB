package export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Constants.fileLocation;

/**
 * Class to export the database to .sql file
 * delimiter used while generating database was " " and "\n"
 */
public class ExportDatabase {

    private String databaseName;
    private String databasePath = fileLocation.LOCAL_PATH + "/";
    private Map<String, Map<String, Integer>> databaseTableDataTypeMap;
    private Map<String, String> databaseTables;
    private Map<String, String> tableInsertions;

    /**
     *
     * @param databaseName
     */
    public ExportDatabase(String databaseName) {
        this.databaseName = databaseName;
        databasePath = databasePath + databaseName;
        this.databaseTableDataTypeMap = new HashMap<>();
        this.databaseTables = new HashMap<>();
        this.tableInsertions = new HashMap<>();
    }

    /**
     * method to execute execute database
     */
    public void executeExport() {
        System.out.println("Exporting " + databaseName + "! Drink Coffee...");
        this.databaseTables = generateTableQueries();
        this.tableInsertions = generateInsertQueries();
        generateExportFile();
    }

    /**
     * method to generate table creation of tables
     *
     * @return
     */
    private Map<String, String> generateTableQueries() {
        Map<String, String> databaseTables = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(this.databasePath + "/dataDictionary.txt"))) {
            Thread.sleep(1000);
            StringBuilder table = new StringBuilder();
            String tableName = null;
            Map<String, Integer> tableDataTypesMap = new HashMap<>();
            for (String line; (line = bufferedReader.readLine()) != null;) {
                String[] parts = line.split("\\s+");
                if (table.length() == 0) {
                    table.append("CREATE TABLE");
                }
                if (line.isEmpty() && table.length() > 0 && tableName != null) {
                    table.setLength(table.length() - 1);
                    table.append(");");
                    databaseTables.put(tableName, table.toString());
                    table = new StringBuilder();
                    this.databaseTableDataTypeMap.put(tableName, tableDataTypesMap);
                    tableName = null;
                    tableDataTypesMap = new HashMap<>();
                    bufferedReader.readLine();
                } else if (!line.isEmpty() && parts.length == 1) {
                    table.append(" `" + parts[0] + "` (");
                    tableName = parts[0];
                } else if (parts.length == 2 || parts.length == 5 || parts.length == 6) {
                    table.append(" `" + parts[0] + "`");
                    table.append(identifyDataType(parts[1]) + ",");
                    tableDataTypesMap.put(parts[0], identifyDataTypeReturnInt(parts[1]));
                } else if (parts.length == 3) {
                    table.append(" `" + parts[0] + "`");
                    table.append(identifyDataType(parts[1]));
                    table.append(" PRIMARY KEY,");
                    tableDataTypesMap.put(parts[0], identifyDataTypeReturnInt(parts[1]));
                }
                // To be uncommented when foreign key implementation
                // else if (parts.length == 5) {
                // table.append(" `" + parts[0] + "`");
                // table.append(identifyDataType(parts[1]));
                // table.append(" FOREIGN KEY,");
                // tableDataTypesMap.put(parts[0], identifyDataTypeReturnInt(parts[1]));
                // } else if (parts.length == 6) {
                // table.append(", `" + parts[0] + "`");
                // table.append(identifyDataType(parts[1]));
                // table.append(" FOREIGN KEY REFERENCES `" + parts[4] + "` (`" + parts[5] +
                // "`),");
                // tableDataTypesMap.put(parts[0], identifyDataTypeReturnInt(parts[1]));
                // }
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
        return databaseTables;
    }

    /**
     * method to generate insert query for tables
     *
     * @return
     */
    private Map<String, String> generateInsertQueries() {
        Map<String, String> tableValueMap = new HashMap<>();
        for (Map.Entry<String, String> entry : databaseTables.entrySet()) {
            StringBuilder values = new StringBuilder();
            values.append("INSERT INTO `" + entry.getKey() + "` VALUES (");
            int numberOfColumns = this.databaseTableDataTypeMap.get(entry.getKey()).size();
            try (BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(this.databasePath + "/" + entry.getKey() + ".txt"))) {
                Thread.sleep(1000);
                int columnCounter = 1;
                for (String line; (line = bufferedReader.readLine()) != null;) {
                    if (line.isEmpty()) {
                        values.append("),");
                        values.append(" (");
                        columnCounter = 1;
                    } else {
                        String valueString = line.substring(line.indexOf(' ') + 1);
                        String column = line.substring(0, line.indexOf(' '));
                        if (this.databaseTableDataTypeMap.get(entry.getKey()).get(column).equals(0)) {
                            values.append(identifyColumnLocation(numberOfColumns, columnCounter, valueString));
                        } else {
                            values.append(
                                    identifyColumnLocation(numberOfColumns, columnCounter, "'" + valueString + "'"));
                        }
                        columnCounter++;
                    }
                }
                values.setLength(values.length() - 3);
                values.append(";");
            } catch (NoSuchFileException | FileNotFoundException e) {
                System.out.println("Table " + entry.getKey() + " not found. Please try again.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Error occurred while fetching tables. Please try again.");
                Thread.currentThread().interrupt();
            }
            tableValueMap.put(entry.getKey(), values.toString());
        }
        // for (Map.Entry<String, String> entry : tableValueMap.entrySet()) {
        // System.out.println(entry.getKey() + " -> " + entry.getValue());
        // }
        return tableValueMap;
    }

    /**
     * method to generate .sql file
     */
    private void generateExportFile() {
        File sqlFile = new File(this.databasePath + "/" + this.databaseName + ".sql");
        try (FileWriter fileWriter = new FileWriter(sqlFile);) {
            fileWriter.write(this.toString());
            System.out.println("Database exported successfully.");
            System.out.println("Export Location: " + this.databasePath + "/" + this.databaseName + ".sql");
        } catch (IOException e) {
            System.out.println("Error occurred while generating .sql file. Please try again.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sqlExportStringBuilder = new StringBuilder();
        sqlExportStringBuilder.append("-- Database: " + this.databaseName + "\n");
        for (Map.Entry<String, String> entry : databaseTables.entrySet()) {
            String tableName = entry.getKey();

            sqlExportStringBuilder.append("\n--\n");
            sqlExportStringBuilder.append("-- Table structure for table `" + tableName + "`\n");
            sqlExportStringBuilder.append("--\n\n");

            sqlExportStringBuilder.append(generateSqlDropTableQuery(entry.getKey()) + "\n");
            sqlExportStringBuilder.append(this.databaseTables.get(entry.getKey()) + "\n");

            sqlExportStringBuilder.append("\n--\n");
            sqlExportStringBuilder.append("-- Dumping data for table `" + tableName + "`\n");
            sqlExportStringBuilder.append("--\n\n");

            sqlExportStringBuilder.append(generateSqlLockTableQuery(entry.getKey()) + "\n");
            sqlExportStringBuilder.append(this.tableInsertions.get(entry.getKey()) + "\n");
            sqlExportStringBuilder.append(generateSqlUnlockTableQuery() + "\n\n");
        }
        sqlExportStringBuilder
                .append("-- Dump completed on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return sqlExportStringBuilder.toString();
    }

    private String generateSqlUnlockTableQuery() {
        return "UNLOCK TABLES;";
    }

    private String generateSqlDropTableQuery(String tableName) {
        return "DROP TABLE IF EXISTS `" + tableName + "`;";
    }

    private String generateSqlLockTableQuery(String tableName) {
        return "LOCK TABLES `" + tableName + "` WRITE;";
    }

    private String identifyColumnLocation(int totalColumns, int currentColumnLocation, String item) {
        if (currentColumnLocation > 1 && currentColumnLocation <= totalColumns) {
            item = ", " + item;
        }
        return item;
    }

    private int identifyDataTypeReturnInt(String item) {
        return item.contains("int") ? 0 : 1;
    }

    private String identifyDataType(String item) {
        return item.contains("int") ? " int" : " varchar(255)";
    }
}

package Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class QueryParser {
    DatabaseOperation dbOperation = new DatabaseOperation();
    Transaction transaction = new Transaction();

    public void parseQuery(String query) {
        System.out.println("current database==" + dbOperation.getCurrentDatabase());

        Matcher createMatcher = CREATE_QUERY_FINAL.matcher(query);
        Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(query);
        Matcher selectMatcher = SELECT_QUERY_FINAL.matcher(query);
        Matcher updateMatcher = UPDATE_QUERY_FINAL.matcher(query);
        Matcher deleteMatcher = DELETE_QUERY_FINAL.matcher(query);
        Matcher truncateMatcher = TRUNCATE_QUERY_FINAL.matcher(query);
        Matcher dropMatcher = DROP_QUERY_FINAL.matcher(query);
        Matcher beginTransactionMatcher = BEGIN_TRANSACTION_QUERY_FINAL.matcher(query);


        if (createDatabaseMatcher.find()) {
            createDatabase(createDatabaseMatcher);
        } else if (useDatabaseMatcher.find()) {
            useDatabase(useDatabaseMatcher);
            System.out.println("Now, current database==" + dbOperation.getCurrentDatabase());
        } else if (createMatcher.find()) {
            createTable(createMatcher);
        } else if (insertMatcher.find()) {
            String tableName = insertMatcher.group(1);
            if (!Lock.checkLock(tableName)) {
                Lock.addLock(tableName);
                insertTable(insertMatcher);
                Lock.removeLock(tableName);
            } else {
                System.out.println("Table Locked. Try again after sometime..");

            }
        } else if (selectMatcher.find()) {
            selectTable(selectMatcher);
        } else if (updateMatcher.find()) {
            String tableName = updateMatcher.group(1);
            if (!Lock.checkLock(tableName)) {
                Lock.addLock(tableName);
                updateTable(updateMatcher);
                Lock.removeLock(tableName);
            } else {
                System.out.println("Table Locked. Try again after sometime..");
            }
        } else if (deleteMatcher.find()) {
            String tableName = deleteMatcher.group(1);
            if (!Lock.checkLock(tableName)) {
                Lock.addLock(tableName);
                deleteTable(deleteMatcher);
                Lock.removeLock(tableName);
            } else {
                System.out.println("Table Locked. Try again after sometime..");
            }
        } else if (truncateMatcher.find()) {
            String tableName = truncateMatcher.group(1);
            if (!Lock.checkLock(tableName)) {
                Lock.addLock(tableName);
                truncateTable(truncateMatcher);
                Lock.removeLock(tableName);
            } else {
                System.out.println("Table Locked. Try again after sometime..");
            }
        } else if (dropMatcher.find()) {
            String tableName = dropMatcher.group(1);
            if (!Lock.checkLock(tableName)) {
                Lock.addLock(tableName);
                dropTable(dropMatcher);
                Lock.removeLock(tableName);
            } else {
                System.out.println("Table Locked. Try again after sometime..");
            }
        } else if (beginTransactionMatcher.find()) {
            Transaction transactionQuery = new Transaction();
            System.out.println("Transaction Begins");
            transactionQuery.processTransaction();
        } else {
            System.out.println("Please enter a valid query");
        }
    }

    public void createDatabase(Matcher createDatabaseMatcher) {
        boolean status = dbOperation.createDb(createDatabaseMatcher.group(1));
        System.out.println("create db status===" + status);
    }

    public void useDatabase(Matcher useDatabaseMatcher) {
        dbOperation.useDb(useDatabaseMatcher.group(1));
    }

    public void createTable(Matcher createMatcher) {
        HashMap<String, String> keySet = new HashMap<>();
        String tableName = createMatcher.group(1);
        String tableSet = createMatcher.group(2);
        String[] colValSet = tableSet.split(",");
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (String columnValue : colValSet) {
            String[] set = columnValue.split(" ");
            columns.add(set[0].strip());
            values.add(set[1].strip());
            if (set.length == 3) {
                if (set[2].strip().equals("PK")) {
                    keySet.put(set[0].strip(), "PK");
                }
            } else if (set.length > 3) {
                keySet.put(set[0].strip(), set[2].strip() + " " + set[3].strip() + " " + set[4].strip());
            }
        }

        TableOperation tableOperation = new TableOperation();
        if (dbOperation.getCurrentDatabase() != null) {
            boolean status = tableOperation.createTable(dbOperation.getCurrentDatabase(), tableName, columns, values, keySet);
            if (status) {
                System.out.println("Successfully creation of new table: " + tableName + " in database: " + dbOperation.getCurrentDatabase());
            } else {
                System.out.println("Failure creation of new table: " + tableName + " in database: " + dbOperation.getCurrentDatabase());
            }
        } else {
            System.out.println("Please select database");
        }
    }

    public void insertTable(Matcher insertMatcher) {
        System.out.println("Query Matched: " + insertMatcher.group(1) + " == " + insertMatcher.group(2) + " == " + insertMatcher.group(3));
        String tableName = insertMatcher.group(1);

        String columnSet = insertMatcher.group(2);
        String[] cols = columnSet.split(",");

        String valueSet = insertMatcher.group(3);
        String[] vals = valueSet.split(",");

        ArrayList<String> columns = new ArrayList<String>(Arrays.asList(cols));
        ArrayList<String> values = new ArrayList<String>(Arrays.asList(vals));

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.insert(dbOperation.getCurrentDatabase(), tableName, columns, values);

            if (status) {
                System.out.println("Successfully inserted into the table");
            } else {
                System.out.println("Failure insertion of new entry in: " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }
    }

    public void selectTable(Matcher selectMatcher) {
        String tableName = selectMatcher.group(8);
        String tableSet = selectMatcher.group(1);
        String[] colValSet = tableSet.split(",");
        ArrayList<String> columns = new ArrayList<>(Arrays.asList(colValSet));
        String conditionColumns = selectMatcher.group(10);
        String conditionValues = selectMatcher.group(11);

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.select(dbOperation.getCurrentDatabase(), tableName, columns, conditionColumns, conditionValues);

            if (status) {
                System.out.println("Successfully performed select query on the " + tableName + " table");
            } else {
                System.out.println("Failure to perform selection query on the " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }
    }

    public void updateTable(Matcher updateMatcher) {
        String tableName = updateMatcher.group(1);
        String tableSet = updateMatcher.group(2);
        String conditionSet = updateMatcher.group(10);
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        if (tableSet.split(", ").length == 1) {
            columns.add(tableSet.split("=")[0]);
            values.add(tableSet.split("=")[1]);
        }
        String[] colValSet = tableSet.split(", ");
        for (String colVal : colValSet) {
            columns.add(colVal.split("=")[0].strip());
            values.add((colVal.split("=")[1]).strip());
        }
        String conditionColumns = conditionSet.split("=")[0].strip();
        String conditionValues = conditionSet.split("=")[1].strip();

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.update(dbOperation.getCurrentDatabase(), tableName, columns, values, conditionColumns, conditionValues);

            if (status) {
                System.out.println("Successfully updated into the table");
            } else {
                System.out.println("Failure updating of new entry in: " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }
    }

    public void deleteTable(Matcher deleMatcher) {
        System.out.println("count===" + deleMatcher.groupCount());
        String tableName = deleMatcher.group(1);
        String conditionColumn = deleMatcher.group(3);
        String conditionValue = deleMatcher.group(4);

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.delete(dbOperation.getCurrentDatabase(), tableName, conditionColumn, conditionValue);

            if (status) {
                System.out.println("Successfully deleted entry from the table");
            } else {
                System.out.println("Failure deleting of new entry in: " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }
    }

    public void truncateTable(Matcher truncateMatcher) {
        String tableName = truncateMatcher.group(1);

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.truncate(dbOperation.getCurrentDatabase(), tableName);

            if (status) {
                System.out.println("Successfully truncated the " + tableName + " table");
            } else {
                System.out.println("Failure to truncate the : " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }

    }

    public void dropTable(Matcher dropMatcher) {
        String tableName = dropMatcher.group(1);

        if (dbOperation.getCurrentDatabase() != null) {
            TableOperation tableOperation = new TableOperation();
            boolean status = tableOperation.drop(dbOperation.getCurrentDatabase(), tableName);

            if (status) {
                System.out.println("Successfully dropped the table");
            } else {
                System.out.println("Failure to drop the " + tableName + " table");
            }
        } else {
            System.out.println("Please select database");
        }

    }
}
package Query;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class Transaction {

    public void processTransaction() {
        ArrayList<String> transactionQueryList = new ArrayList<>();
        QueryOptions qp = new QueryOptions();
        Scanner sc = new Scanner(System.in);
        String query = "";
        while (true) {
            System.out.println("\n1. Execute Transaction query");
            System.out.println("2. Exit");
            System.out.println("Select an option");
            String queryOption = sc.nextLine();
            System.out.println("query options: " + queryOption);
            switch (queryOption) {
                case "1" -> {
                    System.out.println("Please enter your query");
                    query = sc.nextLine();

                    Matcher createMatcher = CREATE_QUERY_FINAL.matcher(query);
                    Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);
                    Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
                    Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(query);
                    Matcher selectMatcher = SELECT_QUERY_FINAL.matcher(query);
                    Matcher updateMatcher = UPDATE_QUERY_FINAL.matcher(query);
                    Matcher deleteMatcher = DELETE_QUERY_FINAL.matcher(query);
                    Matcher truncateMatcher = TRUNCATE_QUERY_FINAL.matcher(query);
                    Matcher dropMatcher = DROP_QUERY_FINAL.matcher(query);
                    Matcher commitTransactionMatcher = COMMIT_TRANSACTION_QUERY_FINAL.matcher(query);
                    Matcher rollbackTransactionMatcher = ROLLBACK_TRANSACTION_QUERY_FINAL.matcher(query);

                    if (createDatabaseMatcher.find() || useDatabaseMatcher.find() || createMatcher.find() || insertMatcher.find() ||
                            selectMatcher.find() || deleteMatcher.find() || updateMatcher.find() || truncateMatcher.find() || dropMatcher.find()) {
                        transactionQueryList.add(query);
                    } else if (commitTransactionMatcher.find()) {
                        System.out.println(transactionQueryList);
                        executeQuery(transactionQueryList);
                        System.out.println("Transaction Completed.");
                        qp.listQueryOptions();
                    } else if (rollbackTransactionMatcher.find()) {
                        transactionQueryList.clear();
                        System.out.println("Transaction Revoked.");
                        qp.listQueryOptions();
                    } else {
                        System.out.println("Please enter a valid query");
                    }
                }
                case "2" -> System.exit(0);
            }

        }
    }

    public void executeQuery(ArrayList<String> transactionQueryList) {
        QueryParser parser = new QueryParser();
        DatabaseOperation dbOperation = new DatabaseOperation();

        for (String query : transactionQueryList) {

            Matcher createMatcher = CREATE_QUERY_FINAL.matcher(query);
            Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);
            Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
            Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(query);
            Matcher selectMatcher = SELECT_QUERY_FINAL.matcher(query);
            Matcher updateMatcher = UPDATE_QUERY_FINAL.matcher(query);
            Matcher deleteMatcher = DELETE_QUERY_FINAL.matcher(query);
            Matcher truncateMatcher = TRUNCATE_QUERY_FINAL.matcher(query);
            Matcher dropMatcher = DROP_QUERY_FINAL.matcher(query);

            if (createDatabaseMatcher.find()) {
                parser.createDatabase(createDatabaseMatcher);
            } else if (useDatabaseMatcher.find()) {
                parser.useDatabase(useDatabaseMatcher);
                System.out.println("Now, current database==" + dbOperation.getCurrentDatabase());
            } else if (createMatcher.find()) {
                parser.createTable(createMatcher);
            } else if (insertMatcher.find()) {
                String tableName = insertMatcher.group(1);
                if (!Lock.checkLock(tableName)) {
                    Lock.addLock(tableName);
                    parser.insertTable(insertMatcher);
                    Lock.removeLock(tableName);
                } else {
                    System.out.println("Table Locked. Try again after sometime..");
                }
            } else if (selectMatcher.find()) {
                parser.selectTable(selectMatcher);
            } else if (updateMatcher.find()) {
                String tableName = updateMatcher.group(1);
                if (!Lock.checkLock(tableName)) {
                    Lock.addLock(tableName);
                    parser.updateTable(updateMatcher);
                    Lock.removeLock(tableName);
                } else {
                    System.out.println("Table Locked. Try again after sometime..");
                }
            } else if (deleteMatcher.find()) {
                String tableName = deleteMatcher.group(1);
                if (!Lock.checkLock(tableName)) {
                    Lock.addLock(tableName);
                    parser.deleteTable(deleteMatcher);
                    Lock.removeLock(tableName);
                } else {
                    System.out.println("Table Locked. Try again after sometime..");
                }
            } else if (truncateMatcher.find()) {
                String tableName = truncateMatcher.group(1);
                if (!Lock.checkLock(tableName)) {
                    Lock.addLock(tableName);
                    parser.truncateTable(truncateMatcher);
                    Lock.removeLock(tableName);
                } else {
                    System.out.println("Table Locked. Try again after sometime..");
                }
            } else if (dropMatcher.find()) {
                String tableName = dropMatcher.group(1);
                if (!Lock.checkLock(tableName)) {
                    Lock.addLock(tableName);
                    parser.dropTable(dropMatcher);
                    Lock.removeLock(tableName);
                } else {
                    System.out.println("Table Locked. Try again after sometime..");
                }
            }
        }
    }
}

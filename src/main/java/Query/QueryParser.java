package Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class QueryParser {
    DatabaseOperation dbOperation = new DatabaseOperation();

    public void parseQuery(String query) {
        System.out.println("current database==" + dbOperation.getCurrentDatabase());

        Matcher createMatcher = CREATE_QUERY_FINAL.matcher(query);
        Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(query);

        if (createDatabaseMatcher.find()) {
            createDatabase(createDatabaseMatcher);
        } else if (useDatabaseMatcher.find()) {
            useDatabase(useDatabaseMatcher);
            System.out.println("Now, current database==" + dbOperation.getCurrentDatabase());
        } else if (createMatcher.find()) {
            createTable(createMatcher);
        } else if (insertMatcher.find()) {
            insertTable(insertMatcher);
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

        for (String columnValue :colValSet) {
            String[] set = columnValue.split(" ");
            columns.add(set[0].strip());
            values.add(set[1].strip());
            if(set.length == 3) {
                if(set[2].strip().equals("PK")) {
                    keySet.put(set[0].strip(), "PK");
                }
            } else if(set.length > 3) {
                keySet.put(set[0].strip(), set[2].strip()+" "+set[3].strip()+" "+set[4].strip());
            }
        }

        TableOperation tableOperation = new TableOperation();
        boolean status = tableOperation.createTable(dbOperation.getCurrentDatabase(), tableName, columns, values, keySet);
        if(status) {
            System.out.println("Successfully creation of new table: "+tableName+" in database: "+dbOperation.getCurrentDatabase());
        } else {
            System.out.println("Failure creation of new table: "+tableName+" in database: "+dbOperation.getCurrentDatabase());
        }
    }

    public void insertTable(Matcher insertMatcher){
        System.out.println("Query Matched: "+ insertMatcher.group(1)+" == "+insertMatcher.group(2) + " == " + insertMatcher.group(3));
        String tableName = insertMatcher.group(1);

        String columnSet = insertMatcher.group(2);
        String[] cols = columnSet.split(",");

        String valueSet = insertMatcher.group(3);
        String[] vals = valueSet.split(",");

        ArrayList<String> columns = new ArrayList<String>(Arrays.asList(cols));
        ArrayList<String> values = new ArrayList<String>(Arrays.asList(vals));

        TableOperation tableOperation = new TableOperation();
        boolean status = tableOperation.insert(dbOperation.getCurrentDatabase(), tableName, columns, values);

        if(status) {
            System.out.println("Successfully inserted into the table");
        } else {
            System.out.println("Failure insertion of new entry in: "+tableName+" table");
        }
    }
}
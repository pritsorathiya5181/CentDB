import Query.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class QueryParserTest {
    @Test
    @Order(1)
    public void testCreateDatabase() {
        QueryParser queryParser = new QueryParser();
        String query = "CREATE DATABASE db1;";
        Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);

        boolean status = false;
        if (createDatabaseMatcher.find()) {
            status = queryParser.createDatabase(createDatabaseMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(2)
    public void testUseDatabase() {
        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        QueryParser queryParser = new QueryParser();

        boolean status = false;
        if (useDatabaseMatcher.find()) {
            status = queryParser.useDatabase(useDatabaseMatcher);
        }
        Assertions.assertTrue(status);
    }

    @Test
    @Order(3)
    public void testCreateTable() {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String createQuery = "CREATE TABLE person (pID int,pName varchar,pAdd varchar);";
        Matcher createMatcher = CREATE_QUERY_FINAL.matcher(createQuery);

        boolean status = false;
        if (createMatcher.find()) {
            status = queryParser.createTable(createMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(4)
    public void testInsertTable() throws FileNotFoundException {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String insertQuery = "INSERT INTO person (pID, pName, pAdd) VALUES (1, p1, home1);";
        Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(insertQuery);

        boolean status = false;
        if (insertMatcher.find()) {
            status = queryParser.insertTable(insertMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(5)
    public void testSelectTable() {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String selectQuery = "SELECT * FROM person;";
        Matcher selectMatcher = SELECT_QUERY_FINAL.matcher(selectQuery);

        boolean status = false;
        if (selectMatcher.find()) {
            status = queryParser.selectTable(selectMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(6)
    public void testUpdateTable() {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String updateQuery = "UPDATE person SET pName=pn1 WHERE pName=p1;";
        Matcher updateMatcher = UPDATE_QUERY_FINAL.matcher(updateQuery);

        boolean status = false;
        if (updateMatcher.find()) {
            status = queryParser.updateTable(updateMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(7)
    public void testDeleteTable() {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String deleteQuery = "DELETE FROM person WHERE pName=pn1;";
        Matcher deleteMatcher = DELETE_QUERY_FINAL.matcher(deleteQuery);

        boolean status = false;
        if (deleteMatcher.find()) {
            status = queryParser.deleteTable(deleteMatcher);
        }

        Assertions.assertTrue(status);
    }

    @Test
    @Order(8)
    public void testDropTable() {
        QueryParser queryParser = new QueryParser();

        String query = "USE db1;";
        Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
        boolean useStatus = false;
        if (useDatabaseMatcher.find()) {
            useStatus = queryParser.useDatabase(useDatabaseMatcher);
        }

        String dropQuery = "DROP TABLE person;";
        Matcher dropMatcher = DROP_QUERY_FINAL.matcher(dropQuery);

        boolean status = false;
        if (dropMatcher.find()) {
            status = queryParser.dropTable(dropMatcher);
        }

        Assertions.assertTrue(status);
    }
}

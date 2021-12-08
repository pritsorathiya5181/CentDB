import Query.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class TransactionTest {
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
}

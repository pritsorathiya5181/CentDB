package Query;

import Analytics.*;

import java.io.IOException;
import java.util.Scanner;

public class QueryOptions {
    public void listQueryOptions() {
        Scanner sc = new Scanner(System.in);
        QueryParser qp = new QueryParser();
        TableOperation tableOperation = new TableOperation();

        System.out.println("Please select the query operation that you want to perform");

        while (true) {
            System.out.println("\n1. Execute SQL query");
            System.out.println("2. Export ERD database");
            System.out.println("3. show analytics");
            System.out.println("4. Exit");
            System.out.println("Select an option");

            String queryOption =sc.nextLine();
            System.out.println("query options: "+queryOption);
            switch (queryOption) {
                case "1" -> {
                    System.out.println("Please enter your query");
                    String query = sc.nextLine();
                    qp.parseQuery(query);
                }
                case "2" -> {
                    System.out.println("Please enter database name");
                    String dbName =  sc.nextLine();
                    try {
                        tableOperation.erd(dbName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "3" -> {
                     Analytics a = new Analytics();
                     a.performAnalytics();
                }
                case "4" -> System.exit(0);
            }
        }
    }
}

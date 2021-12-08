package Query;

import Analytics.Analytics;
import erd.ExportERD;
import export.ExportDatabase;

import java.util.Scanner;

public class QueryOptions {
    private ExportDatabase exportDatabase;

    public void listQueryOptions() {
        Scanner sc = new Scanner(System.in);
        QueryParser qp = new QueryParser();
        TableOperation tableOperation = new TableOperation();
        Analytics a = Analytics.getAnalyticsInstance();
        System.out.println("Please select the query operation that you want to perform");

        while (true) {
            System.out.println("\n1. Execute SQL query");
            System.out.println("2. Export database");
            System.out.println("3. Generate ERD");
            System.out.println("4. Show Analytics");
            System.out.println("5. Exit");
            System.out.println("Select an option");

            String queryOption = sc.nextLine();
            System.out.println("query options: " + queryOption);
            switch (queryOption) {
                case "1" -> {
                    System.out.println("Please enter your query");
                    String query = sc.nextLine();
                    qp.parseQuery(query);
                }
                case "2" -> {
                    System.out.println("Please enter database name");
                    String dbName = sc.nextLine();
                    new ExportDatabase(dbName).executeExport();

                }
                case "3" -> {
                    System.out.println("Please enter database name");
                    String dbName = sc.nextLine();
                    new ExportERD(dbName).executeGenerateERD();
                }
                case "4" -> {
                    a.runner();
                    a.performAnalytics();
                }
                case "5" -> System.exit(0);
            }
        }
    }
}

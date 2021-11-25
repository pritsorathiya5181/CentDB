package Query;

import java.util.Scanner;

public class QueryOptions {
    public void listQueryOptions() {
        Scanner sc = new Scanner(System.in);
        QueryParser qp = new QueryParser();

        System.out.println("Please select the query operation that you want to perform");

        while (true) {
            System.out.println("\n1. Execute SQL query");
            System.out.println("2. Export ERD database");
            System.out.println("3. Exit");
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
                }
                case "3" -> System.exit(0);
            }
        }
    }
}

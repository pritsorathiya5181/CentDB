package Analytics;

import Authentication.UserModel;
import Constants.fileLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


public class Analytics implements AnalyticsService{

    private static Analytics analytics = null;
//    { DB_tables -> [ insert,update,valid,invalid ]  }
    public final Map<String,ArrayList<Integer>> DBrecords = new HashMap<String,ArrayList<Integer>>();
//    { DB -> [totalValid, totalInvalid] }
    public final Map<String,ArrayList<Integer> > DBqueries = new HashMap<String, ArrayList<Integer>>();

    private UserModel user = null;
    private Analytics(UserModel active){
        user = active;
    };

    public static Analytics getAnalyticsInstance(UserModel activeUser){
        if(analytics == null){
            analytics = new Analytics(activeUser);
        }
        return analytics;
    }

    public void DbAnalysis(String Db,boolean valid){
        if(!DBqueries.containsKey(Db)) {
            DBqueries.put(Db,new ArrayList<>(){
                {
                    if (valid) {
                        add(1);
                        add(0);
                    } else {
                        add(0);
                        add(1);
                    }
                }
            });
        }
        else {
            analytics.DBqueries.computeIfPresent(Db,(k,v)->new ArrayList<Integer>(){
                {
                    if (valid) {
                        add(v.get(0) + 1);
                        add(v.get(1));
                    } else {
                        add(v.get(0));
                        add(v.get(1)+1);
                    }
                }
            });
        }
    }

    public void tableAnalytics(String Db,String table,boolean insert,boolean valid){
        String db = Db + "_"+table;
        if(!DBrecords.containsKey(db)){
            DBrecords.put(db,new ArrayList<>(){
                {
                    if (insert ) {
                        add(1);
                        add(0);
                    } else {
                        add(0);
                        add(1);
                    }

                    if (valid) {
                        add(1);
                        add(0);
                    } else {
                        add(0);
                        add(1);
                    }

                }
            });
        }
        else{
            DBrecords.computeIfPresent(db,(k,v)->
               new ArrayList<>(){
                   {
                       if (insert ) {
                           add(v.get(0)+1);
                           add(v.get(1));
                       } else {
                           add(v.get(0));
                           add(v.get(1)+1);
                       }

                       if (valid) {
                           add(v.get(2)+1);
                           add(v.get(3));
                       } else {
                           add(v.get(2));
                           add(v.get(3)+1);
                       }
                   }
               }
            );
        }
    }

    @Override
    public void performAnalytics() {
    boolean newFolderCreated = false;

    //create analytics folder in the source directory if not already there
    File analyticsFolder = new File(fileLocation.ANALYTICS_PATH);
        if(!analyticsFolder.exists()) {
            newFolderCreated = analyticsFolder.mkdir();

        }
        File analyticsDotTxt = null;
        FileWriter writter = null;
        try {
            analyticsDotTxt = new File(fileLocation.ANALYTICS_PATH + "/analytics.txt");
            boolean createdNewAnalyticsFile = analyticsDotTxt.createNewFile();
            writter= new FileWriter(analyticsDotTxt);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // find databases

        File databseDir = new File(fileLocation.LOCAL_PATH );

        File[] databases = databseDir.listFiles();


        // create a analytics object

        if (databases != null) {
            int noOfDatabases = databases.length;
            try {
                writter.append("Total databases in the system : "+noOfDatabases +"\n");
                writter.append("===============================================\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(File database : databases){
                int crrDatabaseTables =0;
                //each database
                int noOfTablesInTheCrrDatabase =0;
                if(database.listFiles() != null && database.listFiles().length >0){
                    noOfTablesInTheCrrDatabase = database.listFiles().length-1;
                }

                try {
                    writter.append("DB:: "+database.getName() + " (total tables in the database-> "+ noOfTablesInTheCrrDatabase + "):\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println(database.getName() + " (total tables in the database-> "+ noOfTablesInTheCrrDatabase + "):");
                if(database.listFiles() != null){
                    crrDatabaseTables=database.listFiles().length;

                    //each table in database
                    //filter data dict
                    int c =0;
                    ArrayList<String> excludeFiles = new ArrayList<>();
                    excludeFiles.add("dataDictionary");
                    excludeFiles.add(database.getName()+".sql");

                    for(File d : database.listFiles()){

//                        if(!d.getName().split(".txt")[0].equals("dataDictionary")){
                        if(!excludeFiles.contains(d.getName().split(".txt")[0])){
                            if(c==0){
                                try {
                                    writter.append("     |\n");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
//                                System.out.println("    |");
                            }

                            try {
                                writter.append("     |->"+d.getName()+"\n");
                                writter.append("         |\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


//                            System.out.println("     |->"+d.getName());
//                            System.out.println("         |");
                            long lines=-1;
                            try (Stream<String> stream = Files.lines(Path.of(d.getPath()), StandardCharsets.UTF_8)) {
//                                lines = stream.count();
                                lines = stream.filter(s->s.length()==0).toList().size() ;
                                if(lines !=0 ){
                                    lines +=1;
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                writter.append("         |-> total records: "+lines+"\n\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            System.out.println("         |-> total records: "+lines);


                            c=1;
                        }
                    }

                    c =0;


                }


            }

            System.out.println("analytics captured, checkout analytics/analytics.txt");
        }


        else {
            System.out.println("no database is yet created");
        }

        try {
            writter.flush();
            writter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void showAnalytics() {

    }

    public void runner(){




        while (true){
            System.out.println("1) enter a query");
            System.out.println("2) perform system wide analytics");
            System.out.println("3) back");

            System.out.print("Enter : ");
            Scanner sc = new Scanner(System.in);
            int option =0;
            try{
                option = sc.nextInt();
            }
            catch (InputMismatchException ignored){
            }

            if(option == 3 || option == 0){
                break;
            }

            switch (option){
                case 1->{
                    //count queries on DB
                    Scanner _sc = new Scanner(System.in);
                    System.out.print("query - >");
                    String query = _sc.nextLine();
                    String [] q = query.split(" ");
                    if(q[0].equals("count") && q[1].equals("queries")){
                        if(DBqueries.keySet().size() > 0){
                            int valid =DBqueries.get(q[3]).get(0);
                            int invalid = DBqueries.get(q[3]).get(1);
                            int total = valid +invalid;
                            System.out.println("User "+
                                    user.getUsername() +
                                    " performed "+
                                    total+
                                    "queries ( valid: " +
                                    valid +
                                    " | invalid: " +
                                    invalid

                            );
                        }
                        else {
                            System.out.println("No DB present");
                        }

                    }
                    //count updates on DB.table
                    //count inserts on DB.table
//                    { DB_tables -> [ insert,update,valid,invalid ]  }
                    if(q[1].equals("updates")){
                        if(DBrecords.keySet().size() > 0){
                            for (Map.Entry<String,ArrayList<Integer>> entry : DBrecords.entrySet()){
                                if(entry.getKey().contains(q[3].split(".")[0])){
                                    if(entry.getKey().contains(q[3].split(".")[1])){
                                        System.out.printf("Total %d updates were performed on %s (Valid: %d | invalid: %d)"
                                                ,entry.getValue().get(1)
                                                ,q[3].split(".")[1]
                                                ,entry.getValue().get(2)
                                                ,entry.getValue().get(3));
                                    }
                                }
                            }
                        }
                        else {
                            System.out.println("No DB present");
                        }

                    }
                    if(q[1].equals("inserts")){
                        if(DBrecords.keySet().size() > 0){
                            for (Map.Entry<String,ArrayList<Integer>> entry : DBrecords.entrySet()){
                                if(entry.getKey().contains(q[3].split(".")[0])){
                                    if(entry.getKey().contains(q[3].split(".")[1])){
                                        System.out.printf("Total %d inserts were performed on %s (Valid: %d | invalid: %d)"
                                                ,entry.getValue().get(1)
                                                ,q[3].split(".")[1]
                                                ,entry.getValue().get(2)
                                                ,entry.getValue().get(3));
                                    }
                                }
                            }
                        }
                        else {
                            System.out.println("No DB present");
                        }

                    }
                }
                case 2->{
                    this.performAnalytics();
                    System.out.println("performing system wide analytics...");
                    System.out.println("Done, check the analytics.txt file");

                }

            }


        }


    }
}

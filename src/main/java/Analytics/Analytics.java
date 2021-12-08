package Analytics;

import Constants.fileLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class Analytics implements AnalyticsService{

    private static Analytics analytics = null;
//    { DB -> [ tables -> [ insert,update,valid,invalid ] ] }
    public final Map<String,ArrayList<ArrayList<String>>> DBrecords = new HashMap<String,ArrayList<ArrayList<String>>>();
//    { DB -> [totalValid, totalInvalid] }
    public final Map<String,ArrayList<Integer> > DBqueries = new HashMap<String, ArrayList<Integer>>();
    private Analytics(){};

    public static Analytics getAnalyticsInstance(){
        if(analytics == null){
            analytics = new Analytics();
        }
        return analytics;
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
        System.out.println("");
        System.out.println(DBqueries.toString());
        System.out.println("");

    }
}

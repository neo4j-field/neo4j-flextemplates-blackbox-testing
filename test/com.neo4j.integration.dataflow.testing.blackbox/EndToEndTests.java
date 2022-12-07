package com.neo4j.integration.dataflow.testing.blackbox;

import com.neo4j.integration.dataflow.testing.blackbox.utils.JobRunnerUtils;
import com.neo4j.integration.dataflow.testing.blackbox.utils.JobCheckResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class EndToEndTests {

    static String NEO4J_CONNECTIONS_FILE="auradb-free-connection.json";

    public static void main(String[] args) {

        String fileNameStr = Thread.currentThread().getStackTrace()[1].getMethodName() + "-" + System.currentTimeMillis();
        System.out.println("Running end-to-end test: " + fileNameStr);
        String shell_file_name="";
        String assertions_file_name="";

        if (args.length==0){
            System.out.println("Please supply the name of the shell script to run.");
            return;
        }
        shell_file_name=args[0];
        System.out.println("Using shell file name: " + shell_file_name);
        if (args.length==2){
            assertions_file_name=args[1];
        } else {
            assertions_file_name=shell_file_name.replace(".sh",".json");
        }
        boolean loadedData=runShellScript(shell_file_name);
        if (!loadedData){
            System.out.println("Could not load data.");
        } else {
            // run tests
            boolean testsRun=checkAssertions(assertions_file_name);
        }
    }

    private static boolean runShellScript(String shell_file_name){

        boolean dataLoaded=false;

        try {
            com.neo4j.integration.dataflow.testing.blackbox.utils.JobRunnerResponse response = JobRunnerUtils.runJobUnderServiceAccount(shell_file_name);
            // Activate service account
            if (response.getHasError()){
                System.out.println("Error running shell script: " + response.getResponseOutput());

            } else {
                System.out.println("Ran shell script, output was: " + response.getResponseOutput());
                System.out.println("Project Id: " + response.getProjectId());
                System.out.println("Job Id: " + response.getJobId());
                System.out.println("Region Id: " + response.getRegion());

                String status="NA";
                for (int i=0;i<200;i++) {
                    JobCheckResponse jobCheckResponse = JobRunnerUtils.checkJobUnderServiceAccount(response);
                    System.out.println("STATUS: "+jobCheckResponse.getStatus());
                    if (jobCheckResponse.getStatus().equals("Done")){
                        System.out.println("Done, now checking results.");
                        dataLoaded=true;
                        break;
                    } else if (jobCheckResponse.getStatus().equals("Failed") || jobCheckResponse.getStatus().equals("Canceled")){
                        System.out.println("Error, stopping.");
                        break;
                    }
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e){
            System.err.println("Error running shell script: " + e.getMessage());
        }

        return dataLoaded;
    }


    private static boolean checkAssertions(String assertion_file_name){

        boolean assertionsSucceeded=true;
        String serverUrl, database, authType, username, password;


        try {
            String currentPath = new java.io.File(".").getCanonicalPath();

            String connectionsFilePath = currentPath + "/shell-scripts/" + NEO4J_CONNECTIONS_FILE;
            java.io.File connectionsFile=new java.io.File(connectionsFilePath);
            if (!connectionsFile.exists()) {
                String errMsg = "Cannot find Neo4j connections file: " + connectionsFile.getAbsoluteFile();
                return false;
            }

            ConnectionParams connectionParams=new com.neo4j.integration.dataflow.testing.blackbox.ConnectionParams(connectionsFile);

            String assertionsFilePath = currentPath + "/shell-scripts/" + assertion_file_name;
            System.out.println("Running file: " + assertionsFilePath);

            java.io.File assertionsFile=new java.io.File(assertionsFilePath);
            if (!assertionsFile.exists()) {
                String errMsg = "Connection file does not exist: " + assertionsFile.getAbsoluteFile();
            }

            //TODO: instantiate driver
            org.neo4j.driver.Driver driver = GraphDatabase.driver(connectionParams.serverUrl,
                    AuthTokens.basic(connectionParams.username, connectionParams.password));

            //TODO: check connection


            JSONObject assertionsJson = new JSONObject(assertionsFile);
            //TODO: loop through assertions


            //TODO: run Cypher


            //TODO: check expected results






            // Check Neo4j connection

            // Loop through assertions






        } catch (Exception e){
            System.err.println("Error running assertions script: " + e.getMessage());
        }

        return assertionsSucceeded;
    }


}

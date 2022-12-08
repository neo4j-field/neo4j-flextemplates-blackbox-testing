package com.neo4j.integration.dataflow.testing.blackbox;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class EndToEndTests {

    static Logger logger =LoggerFactory.getLogger(EndToEndTests.class);

    static String NEO4J_CONNECTIONS_FILE="auradb-free-connection.json";

    public static void main(String[] args) {
        String fileNameStr = Thread.currentThread().getStackTrace()[1].getMethodName() + "-" + System.currentTimeMillis();
        logger.info("Running end-to-end test: " + fileNameStr);
        String shell_file_name="";
        String assertions_file_name="";
        boolean only_run_assertions = true;

        Options options = new Options();
        Option shell = new Option("s", "shell", true, "shell filename");
        shell.setRequired(true);
        options.addOption(shell);

        Option test = new Option("t", "test", true, "test json filename");
        test.setRequired(false);
        options.addOption(test);

        Option onlytest = new Option("o", "onlytest", true, "only test outputs");
        onlytest.setRequired(false);
        options.addOption(onlytest);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            logger.info(e.getMessage());
            formatter.printHelp("blackbox tests", options);
            System.exit(1);
        }

        shell_file_name = cmd.getOptionValue("shell");
        logger.info("Running shell file: " + shell_file_name);
        assertions_file_name = cmd.getOptionValue("test");
        logger.info("Assertions options: " + assertions_file_name);
        if (StringUtils.isEmpty(assertions_file_name)){
            assertions_file_name=shell_file_name.replace(".sh",".json");
        }
        logger.info("Assertions file: " + assertions_file_name);
        only_run_assertions = Boolean.parseBoolean(cmd.getOptionValue("onlytest"));
        logger.info("Only run assertions: " + only_run_assertions);

        if (!only_run_assertions) {
            logger.info("Running shell scripts");
            boolean loadedData = runShellScript(shell_file_name);
            if (!loadedData) {
                logger.info("Could not load data.");
            }
        }
        // run tests
        logger.info("Running tests.");
        boolean testsRun = checkAssertions(assertions_file_name);
        logger.info("Finished: "+testsRun);

    }

    private static boolean runShellScript(String shell_file_name){
        logger.info("Running shell file: " + shell_file_name);
        boolean dataLoaded=false;
        try {
            JobRunnerResponse response = JobRunnerUtils.runJobUnderServiceAccount(shell_file_name);
            // Activate service account
            if (response.getHasError()){
                logger.info("Error running shell script: " + response.getResponseOutput());

            } else {
                logger.info("Ran shell script, output was: " + response.getResponseOutput());
                logger.info("Project Id: " + response.getProjectId());
                logger.info("Job Id: " + response.getJobId());
                logger.info("Region Id: " + response.getRegion());

                String status="NA";
                for (int i=0;i<200;i++) {
                    JobCheckResponse jobCheckResponse = JobRunnerUtils.checkJobUnderServiceAccount(response);
                    logger.info("STATUS: "+jobCheckResponse.getStatus());
                    if (jobCheckResponse.getStatus().equals("Done")){
                        logger.info("Done, now checking results.");
                        dataLoaded=true;
                        break;
                    } else if (jobCheckResponse.getStatus().equals("Failed") || jobCheckResponse.getStatus().equals("Canceled")){
                        logger.info("Error, stopping.");
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

        try {
            String currentPath = new java.io.File(".").getCanonicalPath();

            String connectionsFilePath = currentPath + "/shell-scripts/" + NEO4J_CONNECTIONS_FILE;
            java.io.File connectionsFile=new java.io.File(connectionsFilePath);
            if (!connectionsFile.exists()) {
                logger.info("Cannot find Neo4j connections file: " + connectionsFile.getAbsoluteFile());
                return false;
            }

            ConnectionParams connectionParams=new com.neo4j.integration.dataflow.testing.blackbox.ConnectionParams(connectionsFile);
            String assertionsFilePath = currentPath + "/shell-scripts/" + assertion_file_name;
            java.io.File assertionsFile=new java.io.File(assertionsFilePath);
            if (!assertionsFile.exists()) {
                logger.info("Assertions file does not exist: " + assertionsFile.getAbsoluteFile());
            }
            String assertionsJson=FileUtils.readFileToString(assertionsFile, Charset.defaultCharset());
            JSONArray jsonArray= new JSONArray(assertionsJson);

            try (Driver driver = GraphDatabase.driver(
                    connectionParams.serverUrl,
                    AuthTokens.basic(connectionParams.username, connectionParams.password));
                 Session neo4jSession = driver.session()
            ) {
                logger.info(String.format("Found %d assertions",jsonArray.length()));
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    String cypher=obj.getString("cypher");
                    String expectStr=obj.get("expected")+"";
                    boolean success=cypherAssertionSucceeded(neo4jSession,cypher,expectStr);
                    logger.info("Assertion successful: "+success);
                }
            }

        } catch (Exception e){
            System.err.println("Error running assertions script: " + e.getMessage());
        }

        return assertionsSucceeded;
    }

    private static boolean cypherAssertionSucceeded(Session neo4jSession, String cypher, String expectStr){

        Result result=neo4jSession.run(cypher);
        logger.info("Running cypher: "+cypher+", expecting: "+expectStr);
        if (StringUtils.isNumeric(expectStr)){
            if (Double.parseDouble(expectStr)==result.single().get(0).asDouble()){
                logger.info("Success: "+cypher);
                return true;
            } else {
                logger.info("Failed: "+cypher);
                return false;
            }
        } else {
            if (expectStr.equals(result.single().get(0).asString())){
                logger.info("Success: "+cypher);
                return true;
            } else {
                logger.info("Failed: "+cypher);
                return false;
            }
        }
    }

}

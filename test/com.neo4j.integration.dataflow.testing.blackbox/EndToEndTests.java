package com.neo4j.integration.dataflow.testing.blackbox;

import com.neo4j.integration.dataflow.testing.blackbox.utils.JobRunnerUtils;
import com.neo4j.integration.dataflow.testing.blackbox.utils.JobCheckResponse;
public class EndToEndTests {

    public static void main(String[] args) {

        String fileNameStr = Thread.currentThread().getStackTrace()[1].getMethodName() + "-" + System.currentTimeMillis();
        System.out.println("Running end-to-end test: " + fileNameStr);
        String shell_file_name="regression_test_northwind.sh";

        if (args.length==0){
            System.out.println("Please supply the name of the shell script to run.");
            return;
        }
        shell_file_name=args[0];
        System.out.println("Using shell file name: " + shell_file_name);

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
                boolean dataLoaded=false;
                for (int i=0;i<200;i++) {
                    JobCheckResponse jobCheckResponse = JobRunnerUtils.checkJobUnderServiceAccount(response);
                    System.out.println("STATUS: "+jobCheckResponse.getStatus());
                    if (jobCheckResponse.getStatus().equals("Finished")){
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
    }

}

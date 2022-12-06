package com.neo4j.integration.dataflow.testing.blackbox;

import com.neo4j.integration.dataflow.testing.blackbox.utils.JobRunnerUtils;
import com.neo4j.integration.dataflow.testing.blackbox.utils.JobCheckResponse;
public class EndToEndTests {

    public static void main(String[] args) {

        String fileNameStr = Thread.currentThread().getStackTrace()[1].getMethodName() + "-" + System.currentTimeMillis();
        System.out.println("Running end-to-end test: " + fileNameStr);
        String shell_file_name="create_job_inline_solutions_digital_twin.sh";
        if (args.length>0){
            shell_file_name=args[0];
        }
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
                for (int i=0;i<200;i++) {
                    JobCheckResponse jobCheckResponse = JobRunnerUtils.checkJobUnderServiceAccount(response);
                    System.out.println("STATUS: "+jobCheckResponse.getStatus());
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e){
            System.err.println("Error running shell script: " + e.getMessage());
        }
    }

}

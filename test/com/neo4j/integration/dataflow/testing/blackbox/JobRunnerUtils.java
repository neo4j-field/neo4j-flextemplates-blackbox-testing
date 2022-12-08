package com.neo4j.integration.dataflow.testing.blackbox;

public class JobRunnerUtils {

    public static JobRunnerResponse runJobUnderServiceAccount(String shellFileName) {
         try {
            String currentPath = new java.io.File(".").getCanonicalPath();
            String filePath = currentPath + "/shell-scripts/" + shellFileName;
            System.out.println("Running file: " + filePath);
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String errMsg = "Shell file does not exist: " + file.getAbsoluteFile();
                JobRunnerResponse response = new JobRunnerResponse();
                response.setHasError(true);
                response.setResponseOutput(errMsg);
                return response;
            }
            ProcessBuilder processBuilder = new ProcessBuilder("sh", filePath);
            Process p = processBuilder.start();
            int errCode = p.waitFor();
            boolean hasError = (errCode == 0 ? false : true);
            java.io.InputStream is;
            if (hasError) {
                is = p.getErrorStream();
            } else {
                is = p.getInputStream();
            }
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return parseJobOutput(hasError, builder.toString());
        } catch (Exception e) {
             JobRunnerResponse response = new JobRunnerResponse();
             response.setHasError(true);
             response.setResponseOutput("Error: " + e.getMessage());
             return response;
        }
    }

    public static JobCheckResponse checkJobUnderServiceAccount(JobRunnerResponse response) {

        System.out.println("************checkJobUnderServiceAccount************");

        JobCheckResponse jobCheckResponse=new JobCheckResponse();

        String shellFileName="check-job-status-template.sh";
        try {
            String currentPath = new java.io.File(".").getCanonicalPath();
            String filePath = currentPath + "/shell-scripts/" + shellFileName;
            System.out.println("Running file: " + filePath);
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String errMsg = "Shell file does not exist: " + file.getAbsoluteFile();
                System.err.println("Error: "+errMsg);
                jobCheckResponse.setHasError(true);
                jobCheckResponse.setResponseOutput(errMsg);
                return jobCheckResponse;
            }
            String fileContent= org.apache.commons.io.FileUtils.readFileToString( file, java.nio.charset.Charset.defaultCharset());
            java.util.HashMap<String, String> replacements=new java.util.HashMap();
            replacements.put("JOB_ID",response.getJobId());
            replacements.put("PROJECT_ID",response.getProjectId());
            fileContent = replaceStrings(replacements, fileContent);
            System.out.println("fileContent: "+fileContent);
            String[] fileFragments=fileContent.split(" ");

            java.io.File tmpFile = java.io.File.createTempFile("temp", null);
            java.util.List lines = java.util.Arrays.asList(fileContent.split("\n"));
            org.apache.commons.io.FileUtils.writeLines(tmpFile,lines);
            ProcessBuilder processBuilder = new ProcessBuilder("sh",tmpFile.getAbsolutePath());
            Process p = processBuilder.start();
            int errCode = p.waitFor();
            boolean hasError = (errCode == 0 ? false : true);
            java.io.InputStream is;
            if (hasError) {
                jobCheckResponse.setHasError(true);
                is = p.getErrorStream();
            } else {
                is = p.getInputStream();
            }
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String output=builder.toString();
            System.out.println(output);
            jobCheckResponse.setResponseOutput(output);
            if (!hasError) {
                String jobStatus = parseJobStatus(output);
                jobCheckResponse.setStatus(jobStatus);
            }
        } catch (Exception e) {
            String errMsg="Error: " + e.getMessage();
            jobCheckResponse.setHasError(true);
            jobCheckResponse.setResponseOutput(errMsg);
        }
        return jobCheckResponse;
    }

    public static String replaceStrings(java.util.Map<String,String> map, String str){
        if (map==null || map.isEmpty()){
            return str;
        }
        for (java.util.Map.Entry<String, String> entry : map.entrySet()) {
            str = str.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return str;
    }


    public static String parseJobStatus(String output) {
        String[] jobLines = output.split(System.getProperty("line.separator"));
        int i = 1;
        String trimLine="";
        for (String line : jobLines) {
            trimLine = org.apache.commons.lang3.StringUtils.trim(line);
           // System.out.println(i+++". "+trimLine);
        }
        return trimLine;
    }

    public static JobRunnerResponse parseJobOutput(Boolean hasError, String output) {
        JobRunnerResponse response = new JobRunnerResponse();
        response.setHasError(hasError);
        response.setResponseOutput(output);
        String[] jobLines = output.split(System.getProperty("line.separator"));
        int i = 1;
        for (String line : jobLines) {
            String trimLine = org.apache.commons.lang3.StringUtils.trim(line);
            // System.out.println(i+++". "+trimLine);

            if (trimLine.startsWith("id: ")) {
                response.setJobId(trimLine.split(":")[1].trim());
            } else if (trimLine.startsWith("startTime: ")) {
                response.setStartTime(trimLine.split(":")[1].trim());
            } else if (trimLine.startsWith("name: ")) {
                response.setJobName(trimLine.split(":")[1].trim());
            } else if (trimLine.startsWith("projectId: ")) {
                response.setProjectId(trimLine.split(":")[1].trim());
            } else if (trimLine.startsWith("location: ")) {
                response.setRegion(trimLine.split(":")[1].trim());
            }
        }
        return response;
    }

    /*
    Running: the job is running.
Starting...: the job is created, but the system needs some time to prepare before launching.
Queued: either a FlexRS job is queued or a Flex template job is being launched (which might take several minutes).
Canceling...: the job is being canceled.
Canceled: the job is canceled.
Draining...: the job is being drained.
Drained: the job is drained.
Updating...: the job is being updated.
Updated: the job is updated.
Succeeded: the job has finished successfully.
Failed: the job failed to complete.
     */

}

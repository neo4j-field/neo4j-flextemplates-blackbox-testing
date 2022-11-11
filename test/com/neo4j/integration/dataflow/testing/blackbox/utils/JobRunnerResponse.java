package com.neo4j.integration.dataflow.testing.blackbox.utils;

public class JobRunnerResponse {

    String region = "";
    String projectId = "";
    String jobId = "";
    String jobName = "";
    String startTime = "";
    String responseOutput = "";
    Boolean hasError = false;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getResponseOutput() {
        return responseOutput;
    }

    public void setResponseOutput(String responseOutput) {
        this.responseOutput = responseOutput;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

}

package com.neo4j.integration.dataflow.testing.blackbox;

public class JobCheckResponse {

    String status="ERROR";
    Boolean hasError = false;
    String responseOutput = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getResponseOutput() {
        return responseOutput;
    }

    public void setResponseOutput(String responseOutput) {
        this.responseOutput = responseOutput;
    }


}

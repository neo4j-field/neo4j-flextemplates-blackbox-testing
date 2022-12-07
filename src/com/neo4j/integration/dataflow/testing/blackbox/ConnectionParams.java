package com.neo4j.integration.dataflow.testing.blackbox;


import java.io.Serializable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper object for connection params. */
public class ConnectionParams implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionParams.class);

    public String serverUrl, database, authType, username, password;

    public ConnectionParams(java.io.File connectionFile) {

        try {


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
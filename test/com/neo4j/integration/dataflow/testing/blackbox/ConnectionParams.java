package com.neo4j.integration.dataflow.testing.blackbox;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.Charset;

/** Helper object for connection params. */
public class ConnectionParams implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionParams.class);

    public String serverUrl, database, authType, username, password;

    public ConnectionParams(java.io.File connectionFile) {

        try {
            System.out.println("Reading connection file: "+connectionFile.getAbsolutePath());
            String json= FileUtils.readFileToString(connectionFile, Charset.defaultCharset());
            JSONObject neoConnectionJson = new JSONObject(json);
            serverUrl = neoConnectionJson.getString("server_url");
            if (neoConnectionJson.has("auth_type")) {
                authType = neoConnectionJson.getString("auth_type");
            } else {
                authType = "basic";
            }
            database =
                    neoConnectionJson.has("database") ? neoConnectionJson.getString("database") : "neo4j";
            username = neoConnectionJson.getString("username");
            password = neoConnectionJson.getString("pwd");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

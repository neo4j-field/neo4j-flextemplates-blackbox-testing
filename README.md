# Neo4j Flex Templates / Black Box Testing

This project contains integrated tests harnesses that is used for end-to-end testing within the Neo4j GoogleCloud account.

## Background

This project maintains no dependencies to the [DataFlow template Neo4j fork](https://github.com/neo4j-field/googlecloud-DataflowTemplates) which has its own internal unit tests.

A current challenge with the Beam framework @ Dataflow is tight coupling with project testing and the inability to access non-public resources from tests.  Apparently there are efforts to permit spinning up resources from a shared Google account for end-to-end testing.

This project allows us to run integration tests when we like for whatever builds we like using whatever resources we like.  

## Pre-requisites

Download a GCP service account key and put it somewhere locally.  Register this file in the shell scripts.

    export GOOGLE_APPLICATION_CREDENTIALS="/Users/anthonykrinsky/Documents/Dataflow/service-account-key/neo4j-se-team-201905-sa-key.json"

## Method of execution

Caller syntax:

    com.neo4j.integration.dataflow.testing.blackbox.EndToEndTests <shell-script file> <assertions json file>

The first argument is the shell file to run.  This was the only way to enforce GCP security properly.

The second argument is the assertions file to run.  It will by default use <shell-file>.replaceAll(".sh",".json")

Both files should be in the /shell-scripts project directory.

## Future enhancements

Put code into JUnit test framework
Leverages Jenkins to coordinate processing and uses GitHub credentials.

## Version History
v0.1 - initial release


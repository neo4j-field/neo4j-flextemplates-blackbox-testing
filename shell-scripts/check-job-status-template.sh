export GOOGLE_APPLICATION_CREDENTIALS="/Users/anthonykrinsky/Documents/Dataflow/service-account-key/neo4j-se-team-201905-sa-key.json"
echo "application-credentials"
gcloud auth activate-service-account "1055617507124-compute@developer.gserviceaccount.com" --verbosity="debug" --key-file="/Users/anthonykrinsky/Documents/Dataflow/service-account-key/neo4j-se-team-201905-sa-key.json" --project="neo4j-se-team-201905"
echo "activated-service-account"
gcloud dataflow jobs list --project="${PROJECT_ID}" --filter="id=${JOB_ID}" --format="get(state)"
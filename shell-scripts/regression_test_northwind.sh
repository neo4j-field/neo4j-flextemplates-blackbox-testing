export GOOGLE_APPLICATION_CREDENTIALS="/Users/anthonykrinsky/Documents/Dataflow/service-account-key/neo4j-se-team-201905-sa-key.json"
gcloud auth activate-service-account 1055617507124-compute@developer.gserviceaccount.com --key-file=/Users/anthonykrinsky/Documents/Dataflow/service-account-key/neo4j-se-team-201905-sa-key.json --project=neo4j-se-team-201905
export TEMPLATE_GCS_LOCATION="gs://neo4j-dataflow/flex-templates/images/googlecloud-to-neo4j-image-spec.json"
export REGION=us-central1
export PROJECTID=neo4j-se-team-201905
gcloud dataflow flex-template run "regression-tests-northwinds-`date +%Y%m%d-%H%M%S`" \
    --template-file-gcs-location="$TEMPLATE_GCS_LOCATION" \
    --region "$REGION" \
    --project "$PROJECTID" \
    --parameters jobSpecUri="gs://neo4j-dataflow/job-specs/testing/text/inline-northwind-jobspec.json" \
    --parameters neo4jConnectionUri="gs://neo4j-dataflow-internal/testing/auradb-free-connection.json"


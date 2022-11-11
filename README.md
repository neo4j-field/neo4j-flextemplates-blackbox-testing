# Neo4j Flex Templates / Black Box Testing

This project contains integrated tests harnesses that is used for end-to-end testing within the Neo4j GoogleCloud account.

## Method of execution

This project maintains no dependencies to the [DataFlow template Neo4j fork](https://github.com/neo4j-field/googlecloud-DataflowTemplates) which has its own internal unit tests.

A current challenge with the Beam framework @ Dataflow is tight coupling with project testing and the inability to access non-public resources from tests.  Apparently there are efforts to permit spinning up resources from a shared Google account for end-to-end testing.

This project allows us to run integration tests when we like for whatever builds we like using whatever resources we like.  

It leverages Jenkins to coordinate processing and uses GitHub credentials.

## Version History
v0.1 - initial release


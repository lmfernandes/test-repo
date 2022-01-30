1 - install git bash (can use cmd but commands will need to be changed for windows and kafka initialization as well)
2 - run ./zookeeper-server-start.sh ../config/zookeeper.properties in kafka kafka_2.12-3.1.0 bin folder
3- run ./kafka-server-start.sh ../config/server.properties in kafka kafka_2.12-3.1.0 bin folder
4 - run ./mvnw clean install
5 - run ./mvnw spring-boot:run
6 - Open the link http://localhost:8080/swagger-ui/index.html for an api list to use the api or use postman/curl etc...
7 - Try some endpoints

Field Rules:
id - must be numeric max 19 digits length
groupId - must be numeric max 19 digits length
name - must only have letters and digits (can have spaces)
description - must only have letters and digits (can have spaces)

Api rules:
Kafka api  - for testing kafka messages - no rules it is only to test kafka, the logs for the kafka messages can be seen in the log folder.
Items api - all endpoints besides put and delete may be used without authorization to authorize for this two please write 1234 in the authorize field (this in swagger-ui, if you are using something else please add it to curl, postman etc...).

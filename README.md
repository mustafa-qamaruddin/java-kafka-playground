# User Manual

* Run the compose.yaml services to spin up Kafka broker, 
```shell
 docker-compose up -d
```
* Start Domain Registration Service: AppServer.java (domain-registration/src/main/java/AppServer.java)
* Start Classification Decision Service: ProducerApp.java (producer/src/main/java/ProducerApp.java)
* Start Enrichment Service: EnricherApp.java (enricher/src/main/java/EnricherApp.java)
* To check the results, attach to the running Kafka container, and run the command:
```shell
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic enriched_classification_decisions \
  --from-beginning
```

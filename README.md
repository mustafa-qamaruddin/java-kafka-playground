# 🚀 Kafka Clients App with Producer, Consumer, & RESTful API

# Components

<pre>
.
├── compose.yaml         (Provisions the Zookeeper and Kafka Broker)
├── domain-registration  (Mock: The Domain Registration REST Server)
├── enricher             (The App reading, processing, and writing to Kafka)
└── producer             (Mock: The Domain Classification Producer)
</pre>

# Usage

* Run the compose.yaml services to spin up Kafka broker,

```shell
 docker-compose up -d
```

* Start Domain Registration Service: AppServer.java (domain-registration/src/main/java/AppServer.java)
* Start Classification Decision Service: ProducerApp.java (producer/src/main/java/ProducerApp.java)
* Start Enrichment Service: qubits.EnricherApp.java (enricher/src/main/java/qubits/EnricherApp.java)
* To check the results, attach to the running Kafka container, and run the command, please consider removing
  the `--from-beginning` if only interested in the recent messages:

```shell
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic enriched_classification_decisions \
  --from-beginning
```

* Failed messages should appear under the Dead-Letter Queue `dlq_classification_decisions`

```shell
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic dlq_classification_decisions \
  --from-beginning
```

* To run tests, either from Intellij UI, or use the commands,

```shell
cd url-classifier-enrichment
./gradlew test
```

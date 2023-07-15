package qubits.messaging.consumers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import qubits.dataModels.classificationDecisions.ClassificationDecision;
import qubits.dataModels.classificationDecisions.ClassificationDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ClassificationDecisionConsumerService {
  private final String TOPIC_NAME = "classification_decisions";
  private final int POLL_INTERVAL_MS = 100;
  private final String CONSUMER_GROUP_ID = "cg1";
  private final Consumer<String, ClassificationDecision> consumer;

  public ClassificationDecisionConsumerService(String bootstrapServers) {
    consumer = createConsumer(bootstrapServers);
    consumer.subscribe(List.of(TOPIC_NAME));
  }

  private Consumer<String, ClassificationDecision> createConsumer(String bootstrapServers) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ClassificationDeserializer.class.getName());
    // Batch Configuration
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

    return new KafkaConsumer<>(properties);
  }

  public ConsumerRecords<String, ClassificationDecision> pollTopic() {
    return consumer.poll(Duration.ofMillis(POLL_INTERVAL_MS));
  }

  public void commitOffset() {
    consumer.commitAsync();
  }
}

package qubits.messaging.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;

@Slf4j
public abstract class ProducerService<T> {
  private final String topicName;
  private final KafkaProducer<String, T> producer;

  public ProducerService(String bootstrapServers, String topicName, String valueType) {
    this.topicName = topicName;
    this.producer = createProducer(bootstrapServers, valueType);
  }

  private KafkaProducer<String, T> createProducer(String bootstrapServers, String valueType) {
    // Configure the Kafka producer
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueType);
    // At least once semantics
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    // Batch Configuration
    // 200 bytes per average message times 500 messages
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, "100000");
    props.put(ProducerConfig.LINGER_MS_CONFIG, "100");

    // Create the Kafka producer
    return new KafkaProducer<>(props);
  }

  public void sendList(List<T> objectList) {
    objectList.forEach(this::sendMessage);
  }

  private void sendMessage(T value) {
    try {
      ProducerRecord<String, T> producerRecord = new ProducerRecord<>(topicName, value);
      producer.send(producerRecord, this::producerCallback);
    } catch (Exception e) {
      log.error("Error sending message: {}", e.getMessage());
    }
  }

  private void producerCallback(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      handleFailedMessage(metadata.offset());
      log.error("Error sending message: {}", exception.getMessage());
    } else {
      log.info("Message sent successfully to topic {}", metadata.topic());
    }
  }

  protected abstract void handleFailedMessage(long offset);
}

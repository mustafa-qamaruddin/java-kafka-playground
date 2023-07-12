package messaging.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

@Slf4j
public class ProduceService {
  // TODO make topic configurable parameter
  private static final String TOPIC_NAME = "enriched_classification_decisions";
  KafkaProducer<String, String> producer;

  public ProduceService() {
    // TODO handle logic when connection is shutdown
    this.producer = ProducerFactory.createProducer();
  }

  public void sendJsonMessage(String jsonMessage) {
    try {
      // TODO add At least Once logic with Transaction
      ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, jsonMessage);
      producer.send(record, this::producerCallback);
    } catch (KafkaException e) {
      log.error("Error sending JSON message: " + e.getMessage());
    }
  }

  private void producerCallback(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      log.error("Error sending message: " + exception.getMessage());
    } else {
      log.info("Message sent successfully to topic " + metadata.topic());
    }
  }
}

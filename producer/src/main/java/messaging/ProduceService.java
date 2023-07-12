package messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

@Slf4j
public class ProduceService {
  private static final String TOPIC_NAME = "classification_decisions";
  KafkaProducer<String, String> producer;

  public ProduceService() {
    // TODO handle logic when connection is shutdown
    this.producer = new ProducerFactory().createProducer();
  }

  public void sendJsonMessage(String jsonMessage) {
    try {
      // TODO add exactlyOnce logic with Transaction
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

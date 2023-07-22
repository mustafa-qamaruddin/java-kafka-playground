package messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

@Slf4j
public class ProduceService {
  private static final String TOPIC_NAME = "test";
  KafkaProducer<Integer, String> producer;

  public ProduceService() {
    this.producer = ProducerFactory.createProducer();
  }

  public void sendJsonMessage(int key, String jsonMessage) {
    try {
      ProducerRecord<Integer, String> record = new ProducerRecord<>(TOPIC_NAME, key, jsonMessage);
      producer.send(record, this::producerCallback);
    } catch (KafkaException e) {
      log.error("Error sending JSON message: {}", e.getMessage());
    }
  }

  private void producerCallback(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      log.error("Error sending message: {}", exception.getMessage());
    } else {
      log.info("Message sent successfully to topic {}", metadata.topic());
    }
  }
}

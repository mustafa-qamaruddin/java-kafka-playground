package messaging.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.List;

@Slf4j
public abstract class ProducerService<T> {
  private final String topicName;
  private final KafkaProducer<String, T> producer;

  public ProducerService(String topicName, String valueType) {
    this.topicName = topicName;
    this.producer = ProducerFactory.create(valueType);
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

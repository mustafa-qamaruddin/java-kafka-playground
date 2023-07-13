package messaging.producers;

import enrichedclassifications.EnrichedClassification;
import lombok.extern.slf4j.Slf4j;
import messaging.dlq.DeadLetterQueueService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

import java.util.List;

@Slf4j
public class ProduceService {
  private static final String TOPIC_NAME = "enriched_classification_decisions";
  KafkaProducer<String, EnrichedClassification> producer;

  public ProduceService() {
    this.producer = ProducerFactory.createProducer();
  }

  public void writeToKafka(List<EnrichedClassification> enrichedClassificationList) {
    enrichedClassificationList.forEach(this::sendEnrichedClassificationDecision);
  }

  private void sendEnrichedClassificationDecision(EnrichedClassification enrichedClassification) {
    try {
      ProducerRecord<String, EnrichedClassification> record = new ProducerRecord<>(TOPIC_NAME, enrichedClassification);
      producer.send(record, this::producerCallback);
    } catch (KafkaException e) {
      log.error("Error sending message: {}", e.getMessage());
    }
  }

  private void producerCallback(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      DeadLetterQueueService.getInstance().addToQueue(metadata.offset());
      log.error("Error sending message: {}", exception.getMessage());
    } else {
      log.info("Message sent successfully to topic {}", metadata.topic());
    }
  }
}

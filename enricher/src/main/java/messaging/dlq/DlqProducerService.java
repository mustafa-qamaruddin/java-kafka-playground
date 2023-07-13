package messaging.dlq;

import classifications.ClassificationDecision;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

import java.util.List;

@Slf4j
class DlqProducerService {
  private static final String TOPIC_NAME = "dlq_classification_decisions";
  private final KafkaProducer<String, ClassificationDecision> producer;

  public DlqProducerService() {
    this.producer = DlqProducerFactory.createProducer();
  }

  public void writeToDlq(List<ClassificationDecision> classificationDecisionList) {
    classificationDecisionList.forEach(this::sendClassificationDecision);
  }

  private void sendClassificationDecision(ClassificationDecision classificationDecision) {
    try {
      ProducerRecord<String, ClassificationDecision> record = new ProducerRecord<>(
          TOPIC_NAME, classificationDecision
      );
      producer.send(record, this::producerCallback);
    } catch (KafkaException e) {
      log.error("DLQ: Error sending message: {}", e.getMessage());
    }
  }

  private void producerCallback(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      log.error("DLQ: failed to push message with offset {}", metadata.offset());
    } else {
      log.info("DLQ: Message sent successfully to topic {}", metadata.topic());
    }
  }
}

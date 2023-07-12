package messaging.consumers;

import classifications.ClassificationDecision;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.List;

@Slf4j
public class ConsumeService {
  private final String TOPIC_NAME = "classification_decisions";

  private final Consumer<String, ClassificationDecision> consumer;

  public ConsumeService() {
    consumer = ConsumerFactory.createConsumer();
  }

  public void consumeTopic() {
    consumer.subscribe(List.of(TOPIC_NAME));
    while(true){
      ConsumerRecords<String, ClassificationDecision> records = consumer.poll(Duration.ofMillis(25));

      for (ConsumerRecord<String, ClassificationDecision> record : records){
        log.info("Key: " + record.key() + ", Value: " + record.value());
        log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
        ClassificationDecision classificationDecision = record.value();
        log.info(classificationDecision.getUrl());
        log.info(String.valueOf(classificationDecision.getLogic()));
        log.info(String.valueOf(classificationDecision.getClassification()));
        log.info(classificationDecision.getCreated());
      }
    }
  }
}

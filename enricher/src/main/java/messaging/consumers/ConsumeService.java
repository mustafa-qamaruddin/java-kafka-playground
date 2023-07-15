package messaging.consumers;

import dataModels.classificationDecisions.ClassificationDecision;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.List;

@Slf4j
public class ConsumeService {
  private final String TOPIC_NAME = "classification_decisions";
  private final int POLL_INTERVAL_MS = 100;

  private final Consumer<String, ClassificationDecision> consumer;

  public ConsumeService() {
    consumer = ConsumerFactory.createConsumer();
    consumer.subscribe(List.of(TOPIC_NAME));
  }

  public ConsumerRecords<String, ClassificationDecision> pollTopic() {
    return consumer.poll(Duration.ofMillis(POLL_INTERVAL_MS));
  }

  public void commitOffset() {
    consumer.commitAsync();
  }
}

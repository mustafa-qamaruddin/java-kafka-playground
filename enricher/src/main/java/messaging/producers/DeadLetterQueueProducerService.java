package messaging.producers;

import classifications.ClassificationDecision;
import classifications.ClassificationSerializer;
import lombok.extern.slf4j.Slf4j;
import messaging.common.ProducerService;

@Slf4j
public class DeadLetterQueueProducerService extends ProducerService<ClassificationDecision> {
  private static final String TOPIC_NAME = "dlq_classification_decisions";

  public DeadLetterQueueProducerService() {
    super(TOPIC_NAME, ClassificationSerializer.class.getName());
  }

  @Override
  protected void handleFailedMessage(long offset) {
    // Handle failed message for Dead Letter Queue service
    log.error("DLQ Record with offset {} failed.", offset);
  }
}

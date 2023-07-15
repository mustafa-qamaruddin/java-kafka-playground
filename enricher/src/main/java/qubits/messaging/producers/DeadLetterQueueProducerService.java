package qubits.messaging.producers;

import lombok.extern.slf4j.Slf4j;
import qubits.dataModels.classificationDecisions.ClassificationDecision;
import qubits.dataModels.classificationDecisions.ClassificationSerializer;
import qubits.messaging.common.ProducerService;

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

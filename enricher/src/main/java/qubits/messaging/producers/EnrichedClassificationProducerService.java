package qubits.messaging.producers;

import lombok.extern.slf4j.Slf4j;
import qubits.dataModels.enrichedClassifications.EnrichedClassification;
import qubits.dataModels.enrichedClassifications.EnrichedClassificationSerializer;
import qubits.messaging.failedHandler.FailedHandlerService;

@Slf4j
public class EnrichedClassificationProducerService extends ProducerService<EnrichedClassification> {
  private static final String TOPIC_NAME = "enriched_classification_decisions";

  public EnrichedClassificationProducerService(String bootstrapServers) {
    super(bootstrapServers, TOPIC_NAME, EnrichedClassificationSerializer.class.getName());
  }

  @Override
  protected void handleFailedMessage(long offset) {
    FailedHandlerService.getInstance().addToQueue(offset);
    log.error("Failed to push message with offset {} to topic: {}", offset, TOPIC_NAME);
  }
}

package qubits.messaging.failedHandler;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import qubits.dataModels.classificationDecisions.ClassificationDecision;
import qubits.messaging.producers.DeadLetterQueueProducerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static qubits.EnricherApp.BOOTSTRAP_SERVERS;

// @Singleton
public class FailedHandlerService {
  private static FailedHandlerService instance;
  List<Long> deadLetterQueue;
  Map<Long, ClassificationDecision> messagesOffsets;
  DeadLetterQueueProducerService dlqProducerService;

  private FailedHandlerService(DeadLetterQueueProducerService dlqProducerService) {
    deadLetterQueue = new ArrayList<>();
    messagesOffsets = new HashMap<>();
    this.dlqProducerService = dlqProducerService;
  }

  public static synchronized FailedHandlerService getInstance() {
    if (instance == null) {
      instance = new FailedHandlerService(
          new DeadLetterQueueProducerService(
              BOOTSTRAP_SERVERS
          )
      );
    }
    return instance;
  }

  public void addLookups(ConsumerRecords<String, ClassificationDecision> records) {
    records.forEach(message -> messagesOffsets.put(message.offset(), message.value()));
  }

  public void addToQueue(Long item) {
    deadLetterQueue.add(item);
  }

  public void processQueue() {
    List<ClassificationDecision> failedRecords = deadLetterQueue
        .stream()
        .parallel()
        .map(offset -> messagesOffsets.get(offset))
        .filter(Objects::nonNull)
        .toList();
    dlqProducerService.sendList(failedRecords);
  }

  public void clean() {
    deadLetterQueue.clear();
    messagesOffsets.clear();
  }
}

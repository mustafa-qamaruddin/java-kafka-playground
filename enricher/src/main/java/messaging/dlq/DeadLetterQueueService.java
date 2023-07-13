package messaging.dlq;

import classifications.ClassificationDecision;
import messaging.producers.DeadLetterQueueProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// @Singleton
public class DeadLetterQueueService {
  private static DeadLetterQueueService instance;
  List<Long> deadLetterQueue;
  Map<Long, ClassificationDecision> messagesOffsets;
  DeadLetterQueueProducerService dlqProducerService;

  private DeadLetterQueueService() {
    deadLetterQueue = new ArrayList<>();
    messagesOffsets = new HashMap<>();
    dlqProducerService = new DeadLetterQueueProducerService();
  }

  public static synchronized DeadLetterQueueService getInstance() {
    if (instance == null) {
      instance = new DeadLetterQueueService();
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

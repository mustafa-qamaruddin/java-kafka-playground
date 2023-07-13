import classifications.ClassificationDecision;
import enrichedclassifications.EnrichedClassification;
import enrichers.EnrichService;
import lombok.extern.slf4j.Slf4j;
import messaging.consumers.ConsumeService;
import messaging.consumers.SplitIterator;
import messaging.dlq.DeadLetterQueueService;
import messaging.producers.ProduceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EnricherApp {
  private static final int CHUNK_SIZE = 100;

  public static void main(String[] args) {
    ConsumeService consumeService = new ConsumeService();
    EnrichService enrichService = new EnrichService();
    ProduceService produceService = new ProduceService();
    while (true) {
      // Read from kafka
      ConsumerRecords<String, ClassificationDecision> records = consumeService.pollTopic();
      // map offset -> value
      DeadLetterQueueService.getInstance().addLookups(records);
      // Every hundred work as a batch
      List<ClassificationDecision> recordsList = new ArrayList<>();
      records.forEach(record -> recordsList.add(record.value()));
      SplitIterator<ClassificationDecision> recordsSplitIterator = new SplitIterator<>(
          recordsList, CHUNK_SIZE
      );
      while (recordsSplitIterator.hasNext()) {
        List<ClassificationDecision> chunk = recordsSplitIterator.next();
        // Enrich / Request Domain Registration
        List<EnrichedClassification> enrichedClassificationList = enrichService.enrichClassifications(chunk);
        // Write to Kafka
        produceService.writeToKafka(enrichedClassificationList);
      }
      // mark as read
      consumeService.commitOffset();
    }
  }
}

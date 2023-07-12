import classifications.ClassificationDecision;
import enrichedclassifications.EnrichedClassification;
import enrichers.EnrichService;
import lombok.extern.slf4j.Slf4j;
import messaging.consumers.ConsumeService;
import messaging.consumers.SplitIterator;
import messaging.producers.ProduceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EnricherApp {
  private static final int CHUNK_SIZE = 100;

  // TODO if end of topic reached, but not 100 yet, then enrich anyway
  public static void main(String[] args) {
    ConsumeService consumeService = new ConsumeService();
    ProduceService produceService = new ProduceService();
    while (true) {
      // Read from kafka
      ConsumerRecords<String, ClassificationDecision> records = consumeService.pollTopic();
      // Every hundred work as a batch
      List<ConsumerRecord<String, ClassificationDecision>> recordsList = new ArrayList<>();
      records.forEach(record -> recordsList.add(record));
      SplitIterator<ConsumerRecord<String, ClassificationDecision>> recordsSplitIterator = new SplitIterator<>(
          recordsList, CHUNK_SIZE
      );
      while (recordsSplitIterator.hasNext()) {
        List<ConsumerRecord<String, ClassificationDecision>> chunk = recordsSplitIterator.next();
        // Enrich / Request Domain Registration
        List<EnrichedClassification> enrichedClassificationList = EnrichService.enrichClassifications(chunk);
        // Write to Kafka
        produceService.writeToKafka(enrichedClassificationList);
      }
      // mark as read
      consumeService.commitOffset();
    }
  }
}

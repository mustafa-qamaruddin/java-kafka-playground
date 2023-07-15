import dataModels.classificationDecisions.ClassificationDecision;
import dataModels.enrichedClassifications.EnrichedClassification;
import enrichers.EnrichService;
import lombok.extern.slf4j.Slf4j;
import messaging.admin.AdminService;
import messaging.consumers.ConsumeService;
import messaging.consumers.SplitIterator;
import messaging.dlq.DeadLetterQueueService;
import messaging.producers.EnrichedClassificationProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import rest.DomainRegistrationClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EnricherApp {
  private static final int CHUNK_SIZE = 100;

  public static void main(String[] args) {
    AdminService adminService = new AdminService();
    ConsumeService consumeService = new ConsumeService();
    DomainRegistrationClient domainRegistrationClient = new DomainRegistrationClient();
    EnrichService enrichService = new EnrichService(domainRegistrationClient);
    EnrichedClassificationProducerService produceService = new EnrichedClassificationProducerService();
    // Broker is up?
    if (!adminService.isBrokerUp()) {
      return;
    }
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
        produceService.sendList(enrichedClassificationList);
      }
      // process dead letter queue
      DeadLetterQueueService.getInstance().processQueue();
      // mark as read
      consumeService.commitOffset();
    }
  }
}

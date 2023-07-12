package enrichers;

import classifications.ClassificationDecision;
import enrichedclassifications.EnrichedClassification;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class EnrichService {
  public List<EnrichedClassification> enrichClassifications(
      List<ConsumerRecord<String, ClassificationDecision>> consumerRecordList
  ) {
    return Collections.emptyList();
  }
}

package enrichers;

import classifications.ClassificationDecision;
import domains.DomainInfo;
import domains.DomainRegistrationClient;
import enrichedclassifications.EnrichedClassification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class EnrichService {
  private final DomainRegistrationClient client;

  public EnrichService() {
    client = new DomainRegistrationClient();
  }

  public List<EnrichedClassification> enrichClassifications(
      List<ConsumerRecord<String, ClassificationDecision>> consumerRecordList
  ) {
    // link domains and urls for easy inverse lookups
    Map<String, String> inverseLookUps = new HashMap<>();
    // extract canonical domains to a list
    List<String> domains = consumerRecordList.stream()
        .map(clf -> clf.value().getUrl())
        .map(url -> {
          String domain = DomainExtractor.extract(url);
          if (domain != null) {
            inverseLookUps.put(url, domain);
          }
          return domain;
        })
        .filter(Objects::nonNull)
        .toList();
    // find registration info
    Map<String, DomainInfo> domainInfoMap = client.queryDomainInfos(domains);
    // build enriched objects with age
    return consumerRecordList.stream()
        .map(ConsumerRecord::value)
        .filter(clf -> inverseLookUps.containsKey(clf.getUrl()))
        .map(clf -> EnrichedClassification.builder()
            .classification(clf.getClassification())
            .logic(clf.getLogic())
            .created(clf.getCreated())
            .domainAgeInDays(
                AgeCalculator.calculate(
                    clf.getCreated(),
                    domainInfoMap.get(
                        inverseLookUps.get(
                            clf.getUrl()
                        )
                    ).getCreated()
                )
            )
            .build()).filter(enriched -> ((EnrichedClassification) enriched).getDomainAgeInDays() != -1)
        .toList();
  }
}

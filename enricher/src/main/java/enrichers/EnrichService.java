package enrichers;

import classifications.ClassificationDecision;
import domains.DomainInfo;
import domains.DomainRegistrationClient;
import enrichedclassifications.EnrichedClassification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

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
      List<ClassificationDecision> classificationDecisionList
  ) {
    // link domains and urls for easy inverse lookups
    Map<String, String> inverseLookUps = new HashMap<>();
    // extract canonical domains to a list
    List<String> domains = getDomainsList(classificationDecisionList, inverseLookUps);
    // find registration info
    Map<String, DomainInfo> domainInfoMap = client.queryDomainInfos(domains);
    // build enriched objects with age
    return getEnrichedClassificationList(classificationDecisionList, inverseLookUps, domainInfoMap);
  }

  @NotNull
  private static List<EnrichedClassification> getEnrichedClassificationList(
      List<ClassificationDecision> classificationDecisionList,
      Map<String, String> inverseLookUps,
      Map<String, DomainInfo> domainInfoMap
  ) {
    return classificationDecisionList.stream()
        .filter(clf -> inverseLookUps.containsKey(clf.getUrl()))
        .filter(clf -> domainInfoMap.containsKey(
            inverseLookUps.get(
                clf.getUrl()
            )
        ))
        .map(
            clf -> EnrichedClassification.builder()
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
                .build()
        )
        .filter(enriched -> enriched.getDomainAgeInDays() != -1)
        .toList();
  }

  @NotNull
  private static List<String> getDomainsList(
      List<ClassificationDecision> classificationDecisionList, Map<String, String> inverseLookUps
  ) {
    return classificationDecisionList.stream()
        .map(ClassificationDecision::getUrl)
        .map(url -> {
          String domain = DomainExtractor.extract(url);
          if (domain != null) {
            inverseLookUps.put(url, domain);
          }
          return domain;
        })
        .filter(Objects::nonNull)
        .toList();
  }
}

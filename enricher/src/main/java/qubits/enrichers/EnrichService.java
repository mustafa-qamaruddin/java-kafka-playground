package qubits.enrichers;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import qubits.dataModels.classificationDecisions.ClassificationDecision;
import qubits.dataModels.domainRegistrations.DomainInfo;
import qubits.dataModels.enrichedClassifications.EnrichedClassification;
import qubits.rest.DomainRegistrationClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class EnrichService {
  private final DomainRegistrationClient client;

  public EnrichService(DomainRegistrationClient client) {
    this.client = client;
  }

  public List<EnrichedClassification> enrichClassifications(
      List<ClassificationDecision> classificationDecisionList
  ) {
    // link domains and urls for easy inverse lookups
    Map<String, String> inverseLookUps = new HashMap<>();
    // extract canonical domains to a list
    List<String> domains = getDomainsList(classificationDecisionList, inverseLookUps);
    // find registration info
    Map<String, DomainInfo> domainInfoMap = null;
    if (!domains.isEmpty()) {
      domainInfoMap = client.queryDomainInfos(domains);
    }
    // build enriched objects with age
    return getEnrichedClassificationList(classificationDecisionList, inverseLookUps, domainInfoMap);
  }

  @NotNull
  private static List<EnrichedClassification> getEnrichedClassificationList(
      List<ClassificationDecision> classificationDecisionList,
      Map<String, String> inverseDomainLookup,
      Map<String, DomainInfo> domainInfoMap
  ) {
    if (Objects.isNull(classificationDecisionList)) {
      return Collections.emptyList();
    }
    return classificationDecisionList.stream()
        .map(
            clf -> {
              EnrichedClassification.EnrichedClassificationBuilder builder = EnrichedClassification.builder();
              // Add basic attributes
              builder.url(clf.getUrl())
                  .classification(clf.getClassification())
                  .logic(clf.getLogic())
                  .created(clf.getCreated());

              // Add canonical domain name
              String domainName = inverseDomainLookup.get(clf.getUrl());
              if (!Strings.isNullOrEmpty(domainName)) {
                builder.domainName(
                    domainName
                );

              }

              // Add ageInDays
              if (!Strings.isNullOrEmpty(domainName) && domainInfoMap.containsKey(domainName)) {
                builder.domainAgeInDays(
                    AgeCalculator.calculate(
                        domainInfoMap.get(domainName).getCreated(),
                        clf.getCreated()
                    )
                );
              }

              // return enriched Object
              return builder.build();
            }
        )
        .toList();
  }

  @NotNull
  private static List<String> getDomainsList(
      List<ClassificationDecision> classificationDecisionList, Map<String, String> inverseLookUps
  ) {
    if (Objects.isNull(classificationDecisionList)) {
      return Collections.emptyList();
    }
    return classificationDecisionList.stream()
        .map(ClassificationDecision::getUrl)
        .filter(url -> !url.isEmpty())
        .map(url -> {
          String domain = DomainExtractor.extract(url);
          if (domain != null) {
            inverseLookUps.put(url, domain);
          }
          return domain;
        })
        .toList();
  }
}

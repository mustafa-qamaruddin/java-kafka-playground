package enrichers;

import classifications.ClassificationDecision;
import domains.DomainInfo;
import domains.DomainRegistrationClient;
import enrichedclassifications.EnrichedClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnrichServiceTest {
  @Mock
  private DomainRegistrationClient domainRegistrationClient;

  private EnrichService enrichService;

  @BeforeEach
  void setUp() {
    enrichService = new EnrichService(domainRegistrationClient);
  }

  @Test
  void testSuccessfulEnrichment() {
    // Given
    List<ClassificationDecision> classificationDecisionList = List.of(
        new ClassificationDecision(
            "https://example.com", 321, "2022-01-03T00:00:00Z", 123
        ),
        new ClassificationDecision(
            "https://subdomain.example2.com", 321, "2022-01-05T00:00:00Z", 123
        )
    );
    Mockito.when(
        domainRegistrationClient.queryDomainInfos(
            List.of("example.com", "example2.com")
        )
    ).thenReturn(
        Map.of(
            "example.com", new DomainInfo("2022-01-01T00:00:00Z", "", ""),
            "example2.com", new DomainInfo("2022-01-02T00:00:00Z", "", "")
        )
    );

    // When
    List<EnrichedClassification> enrichedClassificationList = enrichService.enrichClassifications(
        classificationDecisionList
    );

    // Then
    assertEquals(2, enrichedClassificationList.size());
  }
}

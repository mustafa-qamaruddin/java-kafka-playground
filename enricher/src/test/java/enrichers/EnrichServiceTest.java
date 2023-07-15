package enrichers;

import classifications.ClassificationDecision;
import domains.DomainInfo;
import domains.DomainRegistrationClient;
import enrichedclassifications.EnrichedClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
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
            "https://subdomain.example2.com", 654, "2022-01-05T00:00:00Z", 456
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

    // Check the values of each EnrichedClassification object
    EnrichedClassification enrichedClassification1 = enrichedClassificationList.get(0);
    assertEquals("https://example.com", enrichedClassification1.getUrl());
    assertEquals("example.com", enrichedClassification1.getDomainName());
    assertEquals(321, enrichedClassification1.getClassification());
    assertEquals("2022-01-03T00:00:00Z", enrichedClassification1.getCreated());
    assertEquals(123, enrichedClassification1.getLogic());
    assertNotNull(enrichedClassification1.getDomainAgeInDays());
    assertEquals(2, enrichedClassification1.getDomainAgeInDays());

    EnrichedClassification enrichedClassification2 = enrichedClassificationList.get(1);
    assertEquals("https://subdomain.example2.com", enrichedClassification2.getUrl());
    assertEquals("example2.com", enrichedClassification2.getDomainName());
    assertEquals(654, enrichedClassification2.getClassification());
    assertEquals("2022-01-05T00:00:00Z", enrichedClassification2.getCreated());
    assertEquals(456, enrichedClassification2.getLogic());
    assertNotNull(enrichedClassification2.getDomainAgeInDays());
    assertEquals(3, enrichedClassification2.getDomainAgeInDays());
  }
}

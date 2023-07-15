package qubits.enrichers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qubits.dataModels.classificationDecisions.ClassificationDecision;
import qubits.dataModels.domainRegistrations.DomainInfo;
import qubits.dataModels.enrichedClassifications.EnrichedClassification;
import qubits.rest.DomainRegistrationClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
    when(
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

    // Verify the interactions with domainRegistrationClient
    verify(domainRegistrationClient).queryDomainInfos(List.of("example.com", "example2.com"));
  }

  @Test
  void testNoMatchingDomains() {
    // Given
    List<ClassificationDecision> classificationDecisionList = List.of(
        new ClassificationDecision(
            "https://example3.com", 321, "2022-01-03T00:00:00Z", 123
        ),
        new ClassificationDecision(
            "https://example4.com", 654, "2022-01-04T00:00:00Z", 456
        )
    );
    when(domainRegistrationClient.queryDomainInfos(
        List.of("example3.com", "example4.com")
    )).thenReturn(
        Map.of(
            "example4.com", new DomainInfo("2022-01-01T00:00:00Z", "", "")
        )
    );

    // When
    List<EnrichedClassification> enrichedClassificationList = enrichService.enrichClassifications(
        classificationDecisionList
    );

    // Then
    assertEquals(2, enrichedClassificationList.size());

    // Check the values of the first EnrichedClassification object (successful enrichment)
    EnrichedClassification enrichedClassification1 = enrichedClassificationList.get(0);
    assertEquals("https://example3.com", enrichedClassification1.getUrl());
    assertEquals("example3.com", enrichedClassification1.getDomainName());
    assertEquals(321, enrichedClassification1.getClassification());
    assertEquals("2022-01-03T00:00:00Z", enrichedClassification1.getCreated());
    assertEquals(123, enrichedClassification1.getLogic());
    assertNull(enrichedClassification1.getDomainAgeInDays());

    // Check the values of the second EnrichedClassification object (no matching domain)
    EnrichedClassification enrichedClassification2 = enrichedClassificationList.get(1);
    assertEquals("https://example4.com", enrichedClassification2.getUrl());
    assertEquals("example4.com", enrichedClassification2.getDomainName());
    assertEquals(654, enrichedClassification2.getClassification());
    assertEquals("2022-01-04T00:00:00Z", enrichedClassification2.getCreated());
    assertEquals(456, enrichedClassification2.getLogic());
    assertNotNull(enrichedClassification2.getDomainAgeInDays());
    assertEquals(3, enrichedClassification2.getDomainAgeInDays());

    // Verify the interactions with domainRegistrationClient
    verify(domainRegistrationClient).queryDomainInfos(List.of("example3.com", "example4.com"));
  }

  @Test
  void testEmptyClassificationList() {
    // Given
    List<ClassificationDecision> classificationDecisionList = new ArrayList<>();

    // When
    List<EnrichedClassification> enrichedClassificationList = enrichService.enrichClassifications(
        classificationDecisionList
    );

    // Then
    assertTrue(enrichedClassificationList.isEmpty());
    verifyNoInteractions(domainRegistrationClient);
  }

  @Test
  void testNullClassificationList() {
    // Given
    List<ClassificationDecision> classificationDecisionList = null;

    // When
    List<EnrichedClassification> enrichedClassificationList = enrichService.enrichClassifications(
        classificationDecisionList
    );

    // Then
    assertTrue(enrichedClassificationList.isEmpty());
    verifyNoInteractions(domainRegistrationClient);
  }
}

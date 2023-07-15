package enrichers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import qubits.enrichers.DomainExtractor;

class DomainExtractorTest {

  @Test
  void testValidUrlWithWww() {
    // Given
    String url = "https://www.example.com";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testValidUrlWithoutWww() {
    // Given
    String url = "https://example.com";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testUrlWithSubdomains() {
    // Given
    String url = "https://subdomain.example.com";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testUrlWithPathAndQueryParameters() {
    // Given
    String url = "https://www.example.com/path?param=value";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testNullUrl() {
    // Given
    String url = null;

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertNull(result);
  }


  @Test
  void testInvalidUrl() {
    // Given
    String url = "not-a-valid-url";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertNull(result);
  }

  @Test
  void testEmptyStringUrl() {
    // Given
    String url = "";

    // When
    String result = DomainExtractor.extract(url);

    // Then
    Assertions.assertNull(result);
  }
}

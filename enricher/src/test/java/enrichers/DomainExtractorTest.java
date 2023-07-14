package enrichers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExtractorTest {

  @Test
  void testValidUrlWithWww() {
    // Arrange
    String url = "https://www.example.com";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testValidUrlWithoutWww() {
    // Arrange
    String url = "https://example.com";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testUrlWithSubdomains() {
    // Arrange
    String url = "https://subdomain.example.com";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertEquals("subdomain.example.com", result);
  }

  @Test
  void testUrlWithPathAndQueryParameters() {
    // Arrange
    String url = "https://www.example.com/path?param=value";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertEquals("example.com", result);
  }

  @Test
  void testNullUrl() {
    // Arrange
    String url = null;

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertNull(result);
  }


  @Test
  void testInvalidUrl() {
    // Arrange
    String url = "not-a-valid-url";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertNull(result);
  }

  @Test
  void testEmptyStringUrl() {
    // Arrange
    String url = "";

    // Act
    String result = DomainExtractor.extract(url);

    // Assert
    Assertions.assertNull(result);
  }
}

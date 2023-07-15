package qubits.enrichers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AgeCalculatorTest {

  @Test
  void testValidDates() {
    // Given
    String olderDate = "2022-01-01T00:00:00Z";
    String newerDate = "2022-01-03T00:00:00Z";

    // When
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Then
    Assertions.assertEquals(2, result);
  }

  @Test
  void testInvalidDateFormat() {
    // Given
    String olderDate = "2022/01/01";
    String newerDate = "2022/01/03";

    // When
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Then
    Assertions.assertNull(result);
  }

  @Test
  void testOlderDateAfterNewerDate() {
    // Given
    String olderDate = "2022-01-03T00:00:00Z";
    String newerDate = "2022-01-01T00:00:00Z";

    // When
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Then
    Assertions.assertTrue(result < 0);
  }

  @Test
  void testSameDate() {
    // Given
    String olderDate = "2022-01-01T00:00:00Z";
    String newerDate = "2022-01-01T00:00:00Z";

    // When
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Then
    Assertions.assertEquals(0, result);
  }
}

package enrichers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgeCalculatorTest {

  @Test
  void testValidDates() {
    // Arrange
    String olderDate = "2022-01-01T00:00:00Z";
    String newerDate = "2022-01-03T00:00:00Z";

    // Act
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Assert
    Assertions.assertEquals(2, result);
  }

  @Test
  void testInvalidDateFormat() {
    // Arrange
    String olderDate = "2022/01/01";
    String newerDate = "2022/01/03";

    // Act
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Assert
    Assertions.assertNull(result);
  }

  @Test
  void testOlderDateAfterNewerDate() {
    // Arrange
    String olderDate = "2022-01-03T00:00:00Z";
    String newerDate = "2022-01-01T00:00:00Z";

    // Act
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Assert
    Assertions.assertTrue(result < 0);
  }

  @Test
  void testSameDate() {
    // Arrange
    String olderDate = "2022-01-01T00:00:00Z";
    String newerDate = "2022-01-01T00:00:00Z";

    // Act
    Long result = AgeCalculator.calculate(olderDate, newerDate);

    // Assert
    Assertions.assertEquals(0, result);
  }
}

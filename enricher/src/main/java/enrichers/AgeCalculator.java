package enrichers;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class AgeCalculator {
  public long calculate(String olderDate, String newerDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
    LocalDateTime olderDateTime;
    LocalDateTime newerDateTime;
    try {
      olderDateTime = LocalDateTime.parse(olderDate, formatter);
      newerDateTime = LocalDateTime.parse(newerDate, formatter);
    } catch (DateTimeParseException e) {
      return -1;
    }
    return ChronoUnit.DAYS.between(olderDateTime, newerDateTime);
  }
}

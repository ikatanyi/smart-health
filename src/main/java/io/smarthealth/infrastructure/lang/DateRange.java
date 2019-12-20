package io.smarthealth.infrastructure.lang;

import io.smarthealth.infrastructure.exception.APIException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
import org.springframework.util.Assert;

/**
 * A range of dates specified by a start and an end.  Inclusive of both.
 *
 * @author Kelsas
 */
@SuppressWarnings("WeakerAccess")
public class DateRange {
  private final LocalDate start;
  private final LocalDate end;

  public DateRange(final LocalDate start, final LocalDate end) {
    Assert.notNull(start, "Range start must be given.");
    Assert.notNull(start, "Range end must be given.");
    this.start = start;
    this.end = end;
  }

  public Stream<LocalDate> stream() {
    return Stream.iterate(start, (current) -> current.plusDays(1))
        .limit(ChronoUnit.DAYS.between(start, end) + 1);
  }

  public LocalDateTime getStartDateTime() {
    return start.atStartOfDay();
  }

  public LocalDateTime getEndDateTime() {
    return end.plusDays(1).atStartOfDay();
  }

  @Override
  public String toString() {
    return DateConverter.toIsoString(start) + ".." + DateConverter.toIsoString(end);
  }
    @NotNull
  public static DateRange fromIsoStringOrReturnNull(final String isoDateRangeString) {
    if (isoDateRangeString == null) {
      return null;
    } else {
      final String[] dates = isoDateRangeString.split("\\.\\."); 
      if (dates.length != 2)
        throw APIException.badRequest("Date range should consist of exactly two dates.",
            isoDateRangeString);

      try {
        return new DateRange(DateConverter.dateFromIsoString(dates[0]), DateConverter.dateFromIsoString(dates[1]));
      }
      catch(final DateTimeParseException e){
        throw APIException.badRequest("Date {0} must use ISO format",
            isoDateRangeString);
      }
    }
  } 
  
  public static DateRange fromIsoString(final String isoDateRangeString) { 
    if (isoDateRangeString == null) {
      final LocalDate today = LocalDate.now(Clock.systemUTC());
      return new DateRange(today, today);
    } else {
      final String[] dates = isoDateRangeString.split("\\.\\.");
      if (dates.length != 2)
        throw APIException.badRequest("Date range should consist of exactly two dates.",
            isoDateRangeString); 
      try {
          System.err.println(DateConverter.dateFromString(dates[0])+" <->"+ DateConverter.dateFromString(dates[1]));
        return new DateRange(DateConverter.dateFromString(dates[0]), DateConverter.dateFromString(dates[1]));
      }
      catch(final DateTimeParseException e){
        throw APIException.badRequest("Date {0} must use ISO format",
            isoDateRangeString);
      }
    }
  }
}
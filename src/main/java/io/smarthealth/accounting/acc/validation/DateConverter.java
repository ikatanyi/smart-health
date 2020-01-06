package io.smarthealth.accounting.acc.validation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
//import javax.annotation.Nonnull;

public interface DateConverter {

   
    static Long toEpochMillis( final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

   
    static Long toEpochDay( final LocalDate localDate) {
        return localDate.toEpochDay();
    }

   
    static LocalDateTime fromEpochMillis( final Long epochMillis) {
        return LocalDateTime.from(Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC));
    }

   
    static String toIsoString( final Date date) {
        return toIsoString(LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")));
    }

   
    static String toIsoString( final LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

   
    static LocalDateTime fromIsoString( final String isoDateTimeString) {
        return LocalDateTime.from(Instant.parse(isoDateTimeString).atZone(ZoneOffset.UTC));
    }

   
    static LocalDate dateFromIsoString( final String isoDateString) {
        final int zIndex = isoDateString.indexOf("Z");
        final String shortenedString = isoDateString.substring(0, zIndex);
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(shortenedString));
    }

   
    static String toIsoString( final LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_DATE) + "Z";
    }

   
    static LocalDate toLocalDate( final LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }
}

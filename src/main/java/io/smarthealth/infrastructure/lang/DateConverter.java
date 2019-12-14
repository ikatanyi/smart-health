package io.smarthealth.infrastructure.lang;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.annotation.Nonnull;
import org.springframework.util.Assert;

public interface DateConverter {

    @Nonnull
    static Long toEpochMillis(@Nonnull final LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Nonnull
    static Long toEpochDay(@Nonnull final LocalDate localDate) {
        Assert.notNull(localDate, "LocalDate must be given.");
        return localDate.toEpochDay();
    }

    @Nonnull
    static LocalDateTime fromEpochMillis(@Nonnull final Long epochMillis) {
        Assert.notNull(epochMillis, "Epoch milliseconds must be given.");
        return LocalDateTime.from(Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC));
    }

    @Nonnull
    static String toIsoString(@Nonnull final Date date) {
        return toIsoString(LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")));
    }

    @Nonnull
    static String toIsoString(@Nonnull final LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    @Nonnull
    static LocalDateTime fromIsoString(@Nonnull final String isoDateTimeString) {
        Assert.notNull(isoDateTimeString, "ISO date time must be given.");
        return LocalDateTime.from(Instant.parse(isoDateTimeString).atZone(ZoneOffset.UTC));
    }

    @Nonnull
    static LocalDate dateFromIsoString(@Nonnull final String isoDateString) {  //2019-02-01
        Assert.notNull(isoDateString, "ISO date time must be given.");
        final int zIndex = isoDateString.indexOf("Z");
        final String shortenedString = isoDateString.substring(0, zIndex);
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(shortenedString));
    }
    
      @Nonnull
    static LocalDate dateFromString(@Nonnull final String isoDateString) {  //2019-02-01
        Assert.notNull(isoDateString, "ISO date time must be given."); 
        return LocalDate.parse(isoDateString);
//         return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(isoDateString));
    }

    @Nonnull
    static String toIsoString(@Nonnull final LocalDate localDate) {
        Assert.notNull(localDate, "LocalDateTime must be given.");
        return localDate.format(DateTimeFormatter.ISO_DATE) + "Z";
    }

    @Nonnull
    static LocalDate toLocalDate(@Nonnull final LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.toLocalDate();
    }
}

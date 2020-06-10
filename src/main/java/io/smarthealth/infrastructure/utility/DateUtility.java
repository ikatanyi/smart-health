/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public final class DateUtility {

    private DateUtility() {
    }

    public static LocalDate getEndOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getStartOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getEndOfPastMonth() {

        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static Date toDateTime(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        Date output = Date.from(zdt.toInstant());
        return output;
    }
    
    public static Date toDate(LocalDate ldt) {
        if (ldt == null) {
            return null;
        }
        ZonedDateTime zdt = ldt.atStartOfDay().atZone(ZoneId.systemDefault());
        Date output = Date.from(zdt.toInstant());
        return output;
    }
        
}

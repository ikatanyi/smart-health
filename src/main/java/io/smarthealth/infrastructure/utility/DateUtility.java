/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class DateUtility {

    private DateUtility() {
    }

    public static LocalDate getEndOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }
    
    public static LocalDate getStartOfCurrentMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }
    
    public static LocalDate  getEndOfPastMonth() {
        
        
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

}

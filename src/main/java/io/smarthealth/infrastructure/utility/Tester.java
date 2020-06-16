/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author Kelsas
 */
public class Tester {
    public static void main(String[] args) {
        System.err.println(DateUtility.getEndOfCurrentMonth());
        System.err.println(DateUtility.getStartOfCurrentMonth());
        
        System.err.println(DateUtility.getStartOfCurrentMonth().plusDays(-1));
        
        System.err.println(DateUtility.toLocalDateTime(new Date()));
        System.err.println(DateUtility.toDateTime(LocalDateTime.now()));
        
        
        System.err.println(DateFormatUtil.getFormattedRequestDateTime(LocalDateTime.now()));
    }
}

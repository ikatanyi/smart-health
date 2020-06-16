/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import io.smarthealth.infrastructure.lang.Constants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

/**
 *
 * @author Simon.waweru
 */
public class DateFormatUtil {
    public static String generateDateStringInSpecificFormat(String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
	Date date = new Date();
	//System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }
    
    public static String getFormattedRequestDateTime(LocalDateTime date){
        
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.valueOf(Constants.DATE_TIME_PATTERN)));
    }

}

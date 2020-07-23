/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

/**
 *
 * @author kent
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.sf.jasperreports.engine.JRDefaultScriptlet;

public class RoundingHelper extends JRDefaultScriptlet{

    public static String round(BigDecimal value, RoundingMode mode, String pattern) {
        DecimalFormat format = new DecimalFormat(pattern);
        format.setRoundingMode(mode);
        return format.format(value);
    }
    
    public static Integer toInt(Double value){
        if(value!=null)
            return Integer.parseInt(String.valueOf(value));
        else return 0;
    }
    
    public static String dateToString(Object value){
        if(value instanceof LocalDate)
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        else return String.valueOf(value);
    }
    
    public static String dateTimeToString(Object value){
        if(value instanceof LocalDateTime)
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        else return String.valueOf(value);
    }
}

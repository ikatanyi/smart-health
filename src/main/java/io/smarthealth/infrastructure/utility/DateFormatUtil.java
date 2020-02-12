/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.tenant;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Kelsas
 */
public class Tester {
    public static void main(String[] args) {
         LocalDate now = LocalDate.now();
         String month = String.format("%1$" + 2 + "s", now.getMonthValue()).replace(' ', '0');
         String m = StringUtils.leftPad(String.valueOf(now.getMonthValue()), 2, "0");
        String claimNo = String.format("%s%s%s", now.getYear(), m, "00005");
        System.err.println(claimNo);
        
        System.err.println();
    }
}

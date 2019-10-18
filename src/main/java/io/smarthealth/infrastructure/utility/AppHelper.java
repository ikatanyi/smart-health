/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import java.text.DecimalFormat;

/**
 *
 * @author Kelsas
 */
public class AppHelper {
    public static String toFormattedDecimal(Double d){
        return new DecimalFormat("#.##").format(d);
    }
}

package io.smarthealth.clinical.record.service;

/**
 * 
 * BMI Calculator
 * 
 * @author Kelsas
 */
public class BMI {

    public static double calculateBMI(float cm, float kg) {
        double m=cm/100;
        double result = 0;

        result = kg / (Math.pow(m, 2));

        return result;
    }

    public static String getCategory(float result) {
        String category;
        if (result < 15) {
            category = "very severely underweight";
        } else if (result >= 15 && result <= 16) {
            category = "severely underweight";
        } else if (result > 16 && result <= 18.5) {
            category = "underweight";
        } else if (result > 18.5 && result <= 25) {
            category = "normal (healthy weight)";
        } else if (result > 25 && result <= 30) {
            category = "overweight";
        } else if (result > 30 && result <= 35) {
            category = "moderately obese";
        } else if (result > 35 && result <= 40) {
            category = "severely obese";
        } else {
            category = "very severely obese";
        }
        return category;
    }
}

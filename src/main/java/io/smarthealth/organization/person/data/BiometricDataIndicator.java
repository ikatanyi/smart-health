/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

/**
 *
 * @author kelvin.sasaka
 */
public enum BiometricDataIndicator {

    /**
     * Right thumb.
     *
     */
    RT,
    /**
     * Right index finger.
     *
     */
    RI,
    /**
     * Right middle finger.
     *
     */
    RM,
    /**
     * Right ring finger.
     *
     */
    RR,
    /**
     * Right little finger.
     *
     */
    RP,
    /**
     * Left thumb.
     *
     */
    LT,
    /**
     * Left index finger.
     *
     */
    LI,
    /**
     * Left middle finger.
     *
     */
    LM,
    /**
     * Left ring finger.
     *
     */
    LR,
    /**
     * Left little finger.
     *
     */
    LP,
    /**
     * Unknown finger.
     *
     */
    UK;

    public String value() {
        return name();
    }

    public static BiometricDataIndicator fromValue(String v) {
        return valueOf(v);
    }

}

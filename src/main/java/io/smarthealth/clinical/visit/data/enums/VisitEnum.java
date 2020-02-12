/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data.enums;

/**
 *
 * @author Simon.Waweru
 */
public class VisitEnum {

    public enum Status {
        CheckIn,
        CheckOut,
        Admitted,
        Transferred,
        Discharged,
        Booked
    }

    public enum VisitType {
        Outpatient,
        Inpatient
    }

    public enum PaymentMethod {
        Cash,
        Insurance
    }

}

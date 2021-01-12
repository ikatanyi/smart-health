/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.domain.enumeration;

/**
 *
 * @author kent
 */
public enum ReceiverType {
    patient,
    employee,
    DailyVisitPatient,//to provide date
    AllPatients,
    AllSuppliers,
    SpecifiedNumbers//i.e 254777,254889366,254
}

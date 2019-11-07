/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain.enumeration;

/**
 *
 * @author Kennedy.Imbenzi
 */
public enum LabTestState { 
        Scheduled,
        AwaitingSpecimen,
        Accepted,
        Rejected,
        AawitingReview,
        Completed,
        Cancelled,
        AwaitingReview 
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author kent
 */
public interface TatInterface {
    public LocalDate getDate();
    public Integer patientId();
    public String getServicePoint();
    public String getPatientName();
    public LocalDateTime getStart();
    public LocalDateTime getAcknowledged();
    public String getTat();
    public Long getVisitId();
}

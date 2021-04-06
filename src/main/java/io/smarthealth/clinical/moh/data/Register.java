package io.smarthealth.clinical.moh.data;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;

import java.time.LocalDate;

/**
 * @author Kent
 */
public interface Register {

    public String getPatientNumber();

    public String getFullName();

    public Integer getAge();

    public String getGender();

    public String getDiagnosis();

    public String getResidence();

    public java.time.LocalDateTime getDate();

    public LocalDate getSeen();

    public String getCreatedBy();

    public String getPrimaryContact();

    public LocalDate getDateOfBirth();

    public VisitEnum.VisitType getVisitType();

    public String getVisitNumber();

    public String getDiagnosisCode();

    public String getCertainty();

    public String getDiagnosisSubmittedBy();
}

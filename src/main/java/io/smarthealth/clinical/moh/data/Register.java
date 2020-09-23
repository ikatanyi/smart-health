package io.smarthealth.clinical.moh.data;

import java.time.LocalDate;

/**
 *
 * @author Kent
 */
public interface Register {

    public String getPatientNumber();

    public String getFullName();

    public Integer getAge();

    public String getGender();

    public String getDiagnosis();

    public String getResidence();

    public LocalDate getDate();

    public LocalDate getSeen();

    public String getCreatedBy();    
    
}

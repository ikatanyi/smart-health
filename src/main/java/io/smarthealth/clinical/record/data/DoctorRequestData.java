package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Kelsas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface DoctorRequestData {

    public String getPatientNumber();

    public String getVisitNumber();
     
}

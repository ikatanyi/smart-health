package io.smarthealth.clinical.documents.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Kelsas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface OrderRequest {

    public String getPatientNumber();

    public String getVisitNumber();
     
}

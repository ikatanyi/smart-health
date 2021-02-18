package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Ikatanyi
 */
@Data
public class Root{
    @JsonProperty("Claim") 
    public Claim claim;
}

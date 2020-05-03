package io.smarthealth.accounting.billing.data;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopayData { 
   @NotNull(message = "Visit Number is Required")
    private String visitNumber;
   @NotNull(message = "Insurance Scheme is Required")
    private Long schemeId;
    private Boolean visitStart;

    public CopayData(String visitNumber, Long schemeId) {
        this.visitNumber = visitNumber;
        this.schemeId = schemeId;
        this.visitStart= Boolean.TRUE;
    }
    
}

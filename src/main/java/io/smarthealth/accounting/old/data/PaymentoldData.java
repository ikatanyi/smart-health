package io.smarthealth.accounting.old.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentoldData {

    private Long id;

    private String method;

    private Double amount;

    private String referenceCode;

    private String type;

    private String currency;
    //can I know the bank u paying to or from
    
}

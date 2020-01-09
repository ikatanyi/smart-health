package io.smarthealth.accounting.invoice.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class PayerInvoiceId implements Serializable {

    private Long payerId;
    private Long schemeId;
    private Long invoiceId;

}

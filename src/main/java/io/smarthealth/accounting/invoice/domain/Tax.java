package io.smarthealth.accounting.invoice.domain;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class Tax {
    private Long id;
    private Double amount;
    private Double taxRate;
}

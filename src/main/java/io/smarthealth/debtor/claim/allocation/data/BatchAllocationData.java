/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.claim.allocation.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class BatchAllocationData {

    private String receiptNumber;
    private String invoiceNumber;
    private BigDecimal amount;
}

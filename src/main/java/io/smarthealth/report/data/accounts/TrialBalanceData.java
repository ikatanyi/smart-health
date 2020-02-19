/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class TrialBalanceData {

    private BigDecimal debitTotal;
    private BigDecimal creditTotal;

    private String type;
    private String name;
    private String description;
    private String parentLedgerIdentifier;
    private BigDecimal totalValue;
    private String createdOn;
    private String createdBy;
    private String lastModifiedOn;
    private String lastModifiedBy;
}

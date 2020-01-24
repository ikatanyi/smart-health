/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.debtor.claim.processing.domain.enumeration.ProcessType;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "invoice_process_log")
public class InvoiceMerge extends Auditable{
    private Long id;
    private String fromInvoiceNumber;    
    private String toInvoiceNumber;
    @Enumerated(EnumType.STRING)
    private ProcessType type;
    
}

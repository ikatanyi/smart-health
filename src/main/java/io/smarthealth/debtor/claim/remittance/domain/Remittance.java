package io.smarthealth.debtor.claim.remittance.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.debtor.claim.remittance.domain.enumeration.PaymentMode;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.bank.domain.BankAccount;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "payer_remittance")
public class Remittance extends Auditable {
    @ManyToOne
    private Payer payer;
    @ManyToOne
    private BankAccount bankAccount;
    private Double amount;
    private String paymentCode;
    private String transactionId;
    private String remittanceNumber;
    private LocalDate transactionDate;
    private String paymentMode;
    private String receiptNo;
    private String notes;
    private Double balance;    
}

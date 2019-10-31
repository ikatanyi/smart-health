/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import io.smarthealth.accounting.account.domain.enumeration.BillStatus;
import io.smarthealth.auth.domain.User;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "account_patient_bill")
public class PatientBill extends Auditable {

    @ManyToOne
    private Patient patient;
    @ManyToOne
    private Visit visit;
    private String paymentMode;
    @OneToOne
    private Item item;
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private Double amount;
    @OneToOne
    private User user;
    @ManyToOne
    private Department departrment;
    private Double balance;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

}

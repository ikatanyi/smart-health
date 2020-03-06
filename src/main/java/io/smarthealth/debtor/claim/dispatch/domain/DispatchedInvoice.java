package io.smarthealth.debtor.claim.dispatch.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.debtor.claim.dispatch.data.*;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "dispatched_invoice")
public class DispatchedInvoice extends Identifiable {

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_dispatched_invoice_id_invoice_id"))
    private Invoice invoice;
}

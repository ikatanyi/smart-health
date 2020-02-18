package io.smarthealth.debtor.claim.creditNote.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.debtor.claim.allocation.domain.*;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.data.ItemDatas;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteItemData {  
    private Long id;
    private Long billItemId;
    private ItemDatas itemData;
    private Double amount;
}

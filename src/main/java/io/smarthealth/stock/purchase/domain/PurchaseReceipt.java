package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.company.facility.domain.Department;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *  On purchase receipt, do stock entry, update inventory item and make an invoice for supplier
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_receipt")
public class PurchaseReceipt extends Auditable{
   
    private LocalDate transactionDate;
    private String transactionNo;
    private String deliveryNote;
    @ManyToOne
    private Department receivingStore;
    
    @OneToMany(mappedBy = "purchaseReceipt")
    private List<PurchaseReceiptItem> purchaseReceiptLines=new ArrayList<>();
    
            
}

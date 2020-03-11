package io.smarthealth.stock.purchase.domain.specification;

import io.smarthealth.stock.inventory.domain.specification.*;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.stores.domain.Store;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class PurchaseInvoiceSpecification {

    public PurchaseInvoiceSpecification() {
        super();
    }

    public static Specification<PurchaseInvoice> createSpecification(Long supplierId, String invoiceNumber, Boolean paid,PurchaseInvoiceStatus status) {
        
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
             if (invoiceNumber!=null) {
                predicates.add(cb.equal(root.get("invoiceNumber"), invoiceNumber));
            }
             
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
             if (supplierId != null) {
                predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));
            }
             
//             if (item != null) {
//                final String likeExpression = "%" + item + "%";
//                predicates.add(
//                        cb.or(
//                                cb.like(root.get("item").get("itemName"), likeExpression),
//                                cb.like(root.get("item").get("itemCode"), likeExpression)
//                        )
//                );
//            }
             
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

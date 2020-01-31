package io.smarthealth.stock.purchase.domain.specification;

import io.smarthealth.stock.inventory.domain.specification.*;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
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

    public static Specification<PurchaseInvoice> createSpecification(Store store, String item,boolean includeClosed) {
        
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
             if (!includeClosed) {
                predicates.add(cb.equal(root.get("item").get("active"), true));
            }
             
            if (item != null) {
                predicates.add(cb.equal(root.get("item"), item));
            }
            if (store != null) {
                predicates.add(cb.equal(root.get("store"), store));
            }
            
             if (item != null) {
                final String likeExpression = "%" + item + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("item").get("itemName"), likeExpression),
                                cb.like(root.get("item").get("itemCode"), likeExpression)
                        )
                );
            }
             
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

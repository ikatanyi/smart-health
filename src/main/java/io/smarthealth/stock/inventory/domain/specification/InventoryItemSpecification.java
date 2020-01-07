package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.domain.InventoryBalance;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class InventoryItemSpecification {

    public InventoryItemSpecification() {
        super();
    }

    public static Specification<InventoryBalance> createSpecification(Store store, Item item, DateRange range) {
        
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (item != null) {
                predicates.add(cb.equal(root.get("item"), item));
            }
            if (store != null) {
                predicates.add(cb.equal(root.get("store"), store));
            }
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("dateRecorded"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

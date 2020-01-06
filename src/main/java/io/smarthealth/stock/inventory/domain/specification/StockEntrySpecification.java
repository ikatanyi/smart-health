package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class StockEntrySpecification {

    public StockEntrySpecification() {
        super();
    }
  
        public static Specification<StockEntry> createSpecification(String store, String itemName,String referenceNumber,String transactionId, String deliveryNumber, DateRange range, MovementPurpose purpose,MovementType moveType) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            
            if (store != null) {
                predicates.add(cb.equal(root.get("store").get("storeName"), store));
            }
            
             if (itemName != null) {
                predicates.add(cb.equal(root.get("item").get("itemName"), itemName));
            }
             
            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transactionNumber"), transactionId));
            }
             if (deliveryNumber != null) {
                predicates.add(cb.equal(root.get("deliveryNumber"), deliveryNumber));
            }
              if (referenceNumber != null) {
                predicates.add(cb.equal(root.get("referenceNumber"), referenceNumber));
            }
              if (purpose != null) {
                predicates.add(cb.equal(root.get("purpose"), purpose));
            }
              if (moveType != null) {
                predicates.add(cb.equal(root.get("moveType"), moveType));
            }
              if(range!=null){
                  predicates.add(
                     cb.between(root.get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }

              return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
     }
}

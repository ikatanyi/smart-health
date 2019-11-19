package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.accounting.account.domain.specification.*;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.domain.StockMovement;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.stores.domain.Store;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class StocksSpecification {

    public StocksSpecification() {
        super();
    }
  
        public static Specification<StockMovement> createSpecification(String store, String itemName,String referenceNumber,String transactionId, String deliveryNumber, DateRange range, MovementPurpose purpose,MovementType moveType) {
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

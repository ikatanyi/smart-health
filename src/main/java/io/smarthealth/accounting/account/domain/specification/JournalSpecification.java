package io.smarthealth.accounting.account.domain.specification;

import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.enumeration.TransactionType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class JournalSpecification {
    public JournalSpecification(){
        super();
    }
     public static Specification<Journal> createSpecification(String referenceNumber,String transactionId, String transactionType, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            
            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transactionId"), transactionId));
            }
             if (transactionType != null) {
                predicates.add(cb.equal(root.get("transactionType"), TransactionType.valueOf(transactionType)));
            }
              if (referenceNumber != null) {
                predicates.add(cb.equal(root.get("referenceNumber"), referenceNumber));
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

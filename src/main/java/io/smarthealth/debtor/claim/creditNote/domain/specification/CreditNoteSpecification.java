package io.smarthealth.debtor.claim.creditNote.domain.specification;

import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class CreditNoteSpecification {

    public CreditNoteSpecification() {
        super();
    }

    public static Specification<Allocation> createSpecification(String invoiceNo, Long payerId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("invoice").get("payer").get("id"), payerId));
            }
          
            if (invoiceNo != null) {
                predicates.add(cb.equal(root.get("invoice").get("number"), invoiceNo));
            }
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("createdOn"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

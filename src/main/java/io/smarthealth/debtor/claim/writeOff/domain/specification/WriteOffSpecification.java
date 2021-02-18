package io.smarthealth.debtor.claim.writeOff.domain.specification;

import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.infrastructure.lang.DateRange;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 *
 * @author Kelsas
 */
public class WriteOffSpecification {

    public WriteOffSpecification() {
        super();
    }

    public static Specification<Allocation> createSpecification(Long payerId, Long schemeId, String invoiceNo, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("Payer").get("id"), payerId));
            }
            if (schemeId != null) {
                predicates.add(cb.equal(root.get("Scheme").get("id"), schemeId));
            }
            if (invoiceNo != null) {
                predicates.add(cb.equal(root.get("invoiceNo"), invoiceNo));
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

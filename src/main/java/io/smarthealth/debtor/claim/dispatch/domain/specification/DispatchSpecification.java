package io.smarthealth.debtor.claim.dispatch.domain.specification;

import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class DispatchSpecification {

    public DispatchSpecification() {
        super();
    }

    public static Specification<Allocation> createSpecification(Long payerId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("Payer").get("id"), payerId));
            }
          
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("dispatchDate"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

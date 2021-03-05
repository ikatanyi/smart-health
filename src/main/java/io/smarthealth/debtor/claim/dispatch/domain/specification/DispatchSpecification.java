package io.smarthealth.debtor.claim.dispatch.domain.specification;

import io.smarthealth.debtor.claim.dispatch.domain.Dispatch;
import io.smarthealth.infrastructure.lang.DateRange;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 *
 * @author Kennedy.ikatanyi
 */
public class DispatchSpecification {

    public DispatchSpecification() {
        super();
    }

    public static Specification<Dispatch> createSpecification(Long payerId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("Payer").get("id"), payerId));
            }
          
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("dispatchDate"), range.getStartDate(), range.getEndDate())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

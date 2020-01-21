package io.smarthealth.debtor.claim.allocation.domain.specification;

import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class AllocationSpecification {

    public AllocationSpecification() {
        super();
    }

    public static Specification<Allocation> createSpecification(String invoiceNo,String receiptNo, String remittanceNo, Long payerId, Long schemeId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("Invoice").get("PayerId"), payerId));
            }
            if (schemeId != null) {
                predicates.add(cb.equal(root.get("Invoice").get("schemeId"), schemeId));
            }
            if (invoiceNo != null) {
                predicates.add(cb.greaterThan(root.get("invoice").get("invoiceNo"), invoiceNo));
            }
            if (receiptNo != null) {
                predicates.add(cb.greaterThan(root.get("receiptNo"), receiptNo));
            }
            if (remittanceNo != null) {
                predicates.add(cb.greaterThan(root.get("remittanceNo"), remittanceNo));
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

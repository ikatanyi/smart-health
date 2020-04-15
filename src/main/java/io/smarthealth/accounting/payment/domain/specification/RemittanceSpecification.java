package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class RemittanceSpecification {

    private RemittanceSpecification() {
        super();
    }

    public static Specification<Remittance> createSpecification(Long payerId, String receipt, String remittanceNo, Boolean hasBalance, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payerId != null) {
                predicates.add(cb.equal(root.get("payer").get("id"), payerId));
            }
            if (hasBalance != null && hasBalance) {
                predicates.add(cb.greaterThan(root.get("balance"), 0));
            }
            if (receipt != null) {
                predicates.add(cb.equal(root.get("receipt").get("receiptNo"), receipt));
            }
            if (remittanceNo != null) {
                predicates.add(cb.equal(root.get("remittanceNo"), remittanceNo));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("remittanceDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

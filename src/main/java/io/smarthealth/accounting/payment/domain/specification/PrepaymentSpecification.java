package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.PaymentDeposit;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PrepaymentSpecification {

    private PrepaymentSpecification() {
        super();
    }

    public static Specification<PaymentDeposit> createSpecification(String customerNumber, String receiptNo, Boolean hasBalance, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (customerNumber != null) {
                predicates.add(cb.like(root.get("customerNumber"), customerNumber));
            }
            if (receiptNo != null) {
                predicates.add(cb.equal(root.get("receipt").get("receiptNo"), receiptNo));
            }
            if (hasBalance != null) {
                predicates.add(
                        hasBalance
                                ? cb.greaterThan(root.get("balance").as(BigDecimal.class), BigDecimal.ZERO)
                                : cb.equal(root.get("balance").as(BigDecimal.class), BigDecimal.ZERO));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("paymentDate"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

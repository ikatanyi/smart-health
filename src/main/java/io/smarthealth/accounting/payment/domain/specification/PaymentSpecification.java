package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {

    private PaymentSpecification() {
        super();
    }

    public static Specification<Payment> createSpecification(PayeeType creditorType, Long creditorId, String creditor, String transactionNo, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (creditorType != null) {
                predicates.add(cb.equal(root.get("payeeType"), creditorType));
            }
//            if (creditor != null) {
//                predicates.add(cb.equal(root.get("payee"), creditor));
//            }
               if (creditor != null) {
                final String likeExpression = "%" + creditor + "%";
                predicates.add(  cb.like(root.get("payee"), likeExpression)  );
            }
            if (transactionNo != null) {
                predicates.add(cb.equal(root.get("transactionNo"), transactionNo));
            }
            if (creditorId != null) {
                predicates.add(cb.equal(root.get("payeeId"), creditorId));
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

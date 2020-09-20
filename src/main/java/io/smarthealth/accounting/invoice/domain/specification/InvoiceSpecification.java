package io.smarthealth.accounting.invoice.domain.specification;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

    private InvoiceSpecification() {
        super();
    }

    public static Specification<Invoice> createSpecification(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range, Double amountGreaterThan, Boolean filterPastDue, Boolean awaitingSmart, Double amountLessThanOrEqualTo) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>(); 
            if (payer != null) {
                predicates.add(cb.equal(root.get("payer").get("id"), payer));
            }
            if (scheme != null) {
                predicates.add(cb.equal(root.get("scheme").get("id"), scheme));
            }
            
               if (scheme != null) {
                predicates.add(cb.equal(root.get("scheme").get("id"), scheme));
            }

//             if (customer != null) {
//                final String likeExpression = "%" + customer + "%";
//                predicates.add(
//                        cb.or( 
//                                cb.like(root.get("payer").get("legalName"), likeExpression),
//                                cb.like(root.get("payer").get("payerName"), likeExpression)
//                        )
//                );
//            }
            if (invoice != null) {
                predicates.add(cb.equal(root.get("number"), invoice));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("date"), range.getStartDate(), range.getEndDate())
                );
            }
            if (awaitingSmart != null) {
                predicates.add(cb.equal(root.get("awaitingSmart"), awaitingSmart));
            }
            if (amountGreaterThan != null) {
                predicates.add(cb.greaterThan(root.get("balance"), amountGreaterThan));
            }
            if (amountLessThanOrEqualTo != null && amountLessThanOrEqualTo > 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("balance"), amountLessThanOrEqualTo));
            }
//            if (hasBalance != null) {
//                if (hasBalance) {
//                    predicates.add(cb.greaterThan(root.get("balance"), 0));
//                } else {
//                    predicates.add(cb.lessThanOrEqualTo(root.get("balance"), 0));
//                }
//            }
            if (filterPastDue != null && filterPastDue) {
                predicates.add(cb.greaterThan(root.get("dueDate"), LocalDate.now()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

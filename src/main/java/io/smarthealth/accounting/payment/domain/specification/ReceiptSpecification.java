package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ReceiptSpecification {

    private ReceiptSpecification() {
        super();
    }

    public static Specification<Receipt> createSpecification(String payee, String receiptNo, String transactionNo, String shiftNo, Long cashierId, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payee != null) {
                predicates.add(cb.equal(root.get("payer"), payee));
            }
            if (receiptNo != null) {
                predicates.add(cb.equal(root.get("receiptNo"), receiptNo));
            }
            if (transactionNo != null) {
                predicates.add(cb.equal(root.get("transactionNo"), transactionNo));
            }
            if (shiftNo != null) {
                predicates.add(cb.equal(root.get("shift").get("shiftNo"), shiftNo));
            }
            if (cashierId != null) {
                predicates.add(cb.equal(root.get("shift").get("cashier").get("id"), cashierId));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Copayment> getCopayment(String visitNumber, String patientNumber, String invoiceNumber, String receiptNo, Boolean paid, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("patient").get("patientNumber"), patientNumber));
            }
            if (invoiceNumber != null) {
                predicates.add(cb.equal(root.get("invoice").get("number"), invoiceNumber));
            }
            if (receiptNo != null) {
                predicates.add(cb.equal(root.get("receipt").get("receiptNo"), receiptNo));
            }
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("date"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

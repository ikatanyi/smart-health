package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.ReceiptItem;
import io.smarthealth.accounting.payment.domain.ReceiptTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ReceiptSpecification {

    private ReceiptSpecification() {
        super();
    }

    public static Specification<Receipt> createSpecification(String payee, String receiptNo, String transactionNo, String shiftNo, Long servicePoint, Long cashierId, DateRange range, Boolean prepaid) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payee != null) {
                String likeExpression = "%" + payee + "%";
                predicates.add(cb.like(root.get("payer"), likeExpression));
            }
            if (receiptNo != null) {
                predicates.add(cb.equal(root.get("receiptNo"), receiptNo));
            }
            if (transactionNo != null) {
                predicates.add(cb.equal(root.get("transactionNo"), transactionNo));
            }
            //prepayment
            if (prepaid != null) {
                predicates.add(cb.equal(root.get("prepayment"), prepaid));
            }
            if (shiftNo != null) {
                predicates.add(cb.equal(root.get("shift").get("shiftNo"), shiftNo));
            }
            if (cashierId != null) {
                predicates.add(cb.equal(root.get("shift").get("cashier").get("id"), cashierId));
            }
            if (servicePoint != null) {
                predicates.add(cb.equal(root.get("receiptItems").get("item").get("servicePointId"), servicePoint));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<ReceiptItem> createReceiptItemSpecification(Long servicePoint, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (servicePoint != null) {
                predicates.add(cb.equal(root.get("item").get("servicePointId"), servicePoint));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("receipt").get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<ReceiptItem> createVoidedReceiptItemSpecification(Long servicePoint, String patientNumber, String itemCode, Boolean voided, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("item").get("patientBill").get("patient").get("patientNumber"), patientNumber));
            }
            if (servicePoint != null) {
                predicates.add(cb.equal(root.get("item").get("servicePointId"), servicePoint));
            }
            if (itemCode != null) {
                predicates.add(cb.equal(root.get("item").get("item").get("itemCode"), itemCode));
            }
            if (voided != null) {
                predicates.add(cb.equal(root.get("voided"), voided));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("receipt").get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
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

    public static Specification<ReceiptTransaction> createSpecification(String method, String receiptNo, TrnxType type, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (method != null) {
                predicates.add(cb.equal(root.get("method"), method));
            }
            if (receiptNo != null) {
                predicates.add(cb.equal(root.get("receiptNo"), receiptNo));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("receipt").get("transactionDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
package io.smarthealth.accounting.invoice.domain.specification;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceItemSpecification {

    private InvoiceItemSpecification() {
        super();
    }

    public static Specification<InvoiceItem> createSpecification(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>(); 
            if (payer != null) {
                predicates.add(cb.equal(root.get("invoice").get("payer").get("id"), payer));
            }
            if (scheme != null) {
                predicates.add(cb.equal(root.get("invoice").get("scheme").get("id"), scheme));
            }
            
               if (scheme != null) {
                predicates.add(cb.equal(root.get("scheme").get("id"), scheme));
            }
               
            if (invoice != null) {
                predicates.add(cb.equal(root.get("invoice").get("number"), invoice));
            }
            
            predicates.add(cb.equal(root.get("voided"), true));
            
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("invoice").get("patient").get("patientNumber"), patientNo));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("invoice").get("date"), range.getStartDate(), range.getEndDate())
                );
            }
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

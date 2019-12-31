package io.smarthealth.accounting.invoice.domain.specification;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

    private InvoiceSpecification() {
        super();
    }

    public static Specification<Invoice> createSpecification(String customer, String invoice, InvoiceStatus status) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (customer != null) {
                predicates.add(cb.equal(root.get("payer"), customer));
            }
            
             if (customer != null) {
                final String likeExpression = "%" + customer + "%";
                predicates.add(
                        cb.or( 
                                cb.like(root.get("payer").get("legalName"), likeExpression),
                                cb.like(root.get("payer").get("payerName"), likeExpression)
                        )
                );
            }
             
            if (invoice != null) {
                predicates.add(cb.equal(root.get("number"), invoice));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

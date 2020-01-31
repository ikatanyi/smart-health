package io.smarthealth.accounting.invoice.domain.specification;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

    private InvoiceSpecification() {
        super();
    }

    public static Specification<Invoice> createSpecification(Long payer, Long scheme, String invoice, InvoiceStatus status, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payer != null) {
                predicates.add(cb.equal(root.get("Payer").get("id"), payer));
            }
            if (scheme != null) {
                predicates.add(cb.equal(root.get("Scheme").get("id"), payer));
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
            if(range!=null){
                  predicates.add(
                     cb.between(root.get("createdOn"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

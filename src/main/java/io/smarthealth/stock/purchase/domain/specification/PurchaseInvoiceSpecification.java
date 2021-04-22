package io.smarthealth.stock.purchase.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 *
 * @author Kelsas
 */
public class PurchaseInvoiceSpecification {

    public PurchaseInvoiceSpecification() {
        super();
    }

    public static Specification<PurchaseInvoice> createSpecification(Long supplierId, String invoiceNumber, Boolean paid, DateRange range, PurchaseInvoiceStatus status, Boolean approved, String term, PurchaseInvoice.Type transactionType) {
        
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
             if (invoiceNumber!=null) {
                predicates.add(cb.equal(root.get("invoiceNumber"), invoiceNumber));
            }
             
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
             if (approved != null) {
                predicates.add(cb.equal(root.get("approved"), approved));
            }
            
             if (supplierId != null) {
                predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));
            }
              if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDate(), range.getEndDate())
                );
            }
             
             if (term != null|| StringUtils.isNotBlank(term)) {
                final String likeExpression = "%" + term + "%";
                predicates.add( cb.like(root.get("invoiceNumber"), likeExpression) );
            }

             if(transactionType!=null){
                 predicates.add(cb.equal(root.get("type"), transactionType));
             }
             
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

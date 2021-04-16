package io.smarthealth.supplier.domain.specification;

import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.enumeration.SupplierType;
import java.util.ArrayList;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class SupplierSpecification {

    public SupplierSpecification() {
        super();
    }
    
    public static Specification<Supplier> createSpecification(final SupplierType type, String term, Boolean includeClosed) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (includeClosed!=null && !includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("supplierType"), type));
            } 
            if (term != null) {
               String likeExpression = "%" + term.toLowerCase() + "%";
                predicates.add(cb.like(root.get("supplierName"), likeExpression));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

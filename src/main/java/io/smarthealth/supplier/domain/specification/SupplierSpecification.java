package io.smarthealth.supplier.domain.specification;

import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.enumeration.SupplierType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class SupplierSpecification {

    public SupplierSpecification() {
        super();
    }
    
    public static Specification<Supplier> createSpecification(final SupplierType type, final String term, final boolean includeClosed) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("supplierType"), type));
            } 
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("supplierName"), likeExpression),
                                cb.like(root.get("legalName"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

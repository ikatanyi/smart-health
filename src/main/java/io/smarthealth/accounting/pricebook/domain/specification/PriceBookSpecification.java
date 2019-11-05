package io.smarthealth.accounting.pricebook.domain.specification;

import io.smarthealth.accounting.pricebook.domain.PriceBook;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class PriceBookSpecification {

    public PriceBookSpecification() {
        super();
    }

    public static Specification<PriceBook> createSpecification(PriceBook.Type type, PriceBook.PriceBookType booktype, boolean includeClosed) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (booktype != null) {
                predicates.add(cb.equal(root.get("priceBookType"), booktype));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

package io.smarthealth.accounting.pricelist.domain.specification;

import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.domain.PriceBookItem;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
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

    public static Specification<PriceBook> createSpecification(PriceCategory category, PriceType type, boolean includeClosed) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("priceType"), type));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("priceCategory"), category));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<PriceBookItem> createPriceBookItemSpecification(Long priceBookId, String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (priceBookId != null) {
                predicates.add(cb.equal(root.get("priceBook").get("id"), priceBookId));
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("item").get("itemName"), likeExpression),
                                cb.like(root.get("item").get("itemCode"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

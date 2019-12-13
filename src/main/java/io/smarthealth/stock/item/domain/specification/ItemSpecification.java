package io.smarthealth.stock.item.domain.specification;

import io.smarthealth.stock.item.domain.Item;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class ItemSpecification {

    public ItemSpecification() {
        super();
    }

    public static Specification<Item> createSpecification(String category, String type, final boolean includeClosed, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("itemType"), type));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("itemName"), likeExpression),
                                cb.like(root.get("itemCode"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

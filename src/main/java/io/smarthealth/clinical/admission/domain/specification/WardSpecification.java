package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class WardSpecification {

    public WardSpecification() {
        super();
    }

    public static Specification<Ward> createSpecification(String name, final boolean active, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!active) {
                predicates.add(cb.equal(root.get("isActive"), true));
            }
            if (name!=null) {
                predicates.add(cb.equal(root.get("name"), name));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("name"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

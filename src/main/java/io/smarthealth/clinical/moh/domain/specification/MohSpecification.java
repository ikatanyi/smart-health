package io.smarthealth.clinical.moh.domain.specification;

import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import io.smarthealth.clinical.moh.domain.Moh;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class MohSpecification {

    public MohSpecification() {
        super();
    }

    public static Specification<Moh> createMohSpecification(Boolean a705, Boolean b705, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!a705) {
                predicates.add(cb.equal(root.get("a705"), true));
            }

            if (!b705) {
                predicates.add(cb.equal(root.get("b705"), true));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("description"), likeExpression)
                        )
                );
            }
            query.orderBy(cb.asc(root.get("code")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

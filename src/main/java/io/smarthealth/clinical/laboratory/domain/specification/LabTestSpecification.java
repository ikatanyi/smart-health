package io.smarthealth.clinical.laboratory.domain.specification;

import io.smarthealth.clinical.laboratory.domain.LabTest;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class LabTestSpecification {

    public LabTestSpecification() {
        super();
    }

    public static Specification<LabTest> createSpecification(String queryTest, String displine) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (displine != null) {
                predicates.add(cb.equal(root.get("displine").get("displineName"), displine));
            }
            if (queryTest != null) {
                final String likeExpression = "%" + queryTest + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("service").get("itemName"), likeExpression),
                                 cb.like(root.get("service").get("itemCode"), likeExpression),
                                cb.like(root.get("testName"), likeExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import io.smarthealth.clinical.admission.domain.BedCharge;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class BedChargeSpecification {

    public BedChargeSpecification() {
        super();
    }

    public static Specification<BedCharge> createSpecification(final Long bedTypeId, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (bedTypeId!=null) {
                predicates.add(cb.equal(root.get("bedType").get("id"), bedTypeId));
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("item").get("itemName"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

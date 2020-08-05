package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class BedSpecification {

    public BedSpecification() {
        super();
    }

    public static Specification<Bed> createSpecification(final String name, final Status status, final boolean active, final Long roomId,  final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!active) {
                predicates.add(cb.equal(root.get("isActive"), true));
            }
            if (name!=null) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (status!=null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if(roomId!=null){
                predicates.add(cb.equal(root.get("room").get("id"), roomId));
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

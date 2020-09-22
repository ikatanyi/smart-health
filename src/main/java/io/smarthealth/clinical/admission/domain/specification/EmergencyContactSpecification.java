package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import io.smarthealth.clinical.admission.domain.EmergencyContact;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class EmergencyContactSpecification {

    public EmergencyContactSpecification() {
        super();
    }

    public static Specification<EmergencyContact> createSpecification(final String name, final String patientId, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            
            if (name!=null) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (patientId!=null) {
                predicates.add(cb.equal(root.get("admission").get("patient").get("patientNumber"), patientId));
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

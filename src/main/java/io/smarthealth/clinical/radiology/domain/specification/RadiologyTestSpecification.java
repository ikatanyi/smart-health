package io.smarthealth.clinical.radiology.domain.specification;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.Category;
import io.smarthealth.clinical.radiology.domain.enumeration.Gender;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class RadiologyTestSpecification {

    public RadiologyTestSpecification() {
        super();
    }

    public static Specification<RadiologyTest> createSpecification(Boolean supervisorConfirmation, Gender gender, Category category, String name) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (supervisorConfirmation != null) {
                predicates.add(cb.equal(root.get("supervisorConfirmation"), supervisorConfirmation));
            }
            if (gender != null) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (name != null) {
                final String likeExpression = "%" + name + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("scanName"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

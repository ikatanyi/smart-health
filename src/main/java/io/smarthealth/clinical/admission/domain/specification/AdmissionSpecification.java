package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AdmissionSpecification {

    public AdmissionSpecification() {
        super();
    }

    public static Specification<Admission> createSpecification(final String admissionNo, final Ward ward, final Room room, final Bed bed, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (admissionNo != null) {
                predicates.add(cb.equal(root.get("admissionNo"), admissionNo));
            }
            if (ward != null) {
                predicates.add(cb.equal(root.get("ward"), ward));
            }
            if (room != null) {
                predicates.add(cb.equal(root.get("room"), room));
            }
            if (bed != null) {
                predicates.add(cb.equal(root.get("bed"), bed));
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("admissionNo"), likeExpression),
                                cb.like(root.get("patient").get("patientNumber"), likeExpression),
                                cb.like(root.get("patient").get("fullName"), likeExpression),
                                cb.like(root.get("visitNumber"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

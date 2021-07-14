package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.AdmissionRequest;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.security.domain.User;
import org.apache.tools.ant.taskdefs.War;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

public class AdmissionRequestSpecification {

    public AdmissionRequestSpecification() {
        super();
    }

    public static Specification<AdmissionRequest> createSpecification(final String patientName,
                                                                      final FullFillerStatusType status,
                                                                      final String requestedByusername,
                                                                      final Long wardId,
                                                                      final DateRange requestDateRange
    ) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (requestedByusername != null) {
                predicates.add(cb.equal(root.get("user").get("username"), requestedByusername));
            }
            if (wardId != null) {
                predicates.add(cb.equal(root.get("ward").get("id"), wardId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("fulfillerStatus"), status));
            }
            if (patientName != null) {
                final String likeExpression = "%" + patientName + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("patient").get("fullName"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

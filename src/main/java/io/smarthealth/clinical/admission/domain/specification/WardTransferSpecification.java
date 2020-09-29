package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.domain.WardTransfer;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class WardTransferSpecification {

    public WardTransferSpecification() {
        super();
    }

    public static Specification<WardTransfer> createSpecification(final Long wardId, Long roomId, Long bedId, String patientNo, String admissionNo, DateRange range, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (wardId != null) {
                predicates.add(cb.equal(root.get("ward").get("id"), wardId));
            }
            if (roomId != null) {
                predicates.add(cb.equal(root.get("room").get("id"), roomId));
            }
            if (bedId != null) {
                predicates.add(cb.equal(root.get("bed").get("id"), bedId));
            }
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }
             if (admissionNo != null) {
                predicates.add(cb.equal(root.get("admission").get("admissionNo"), admissionNo));
            }
             if (range != null) {
                predicates.add(                      
                        cb.between(root.get("transferDatetime"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
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

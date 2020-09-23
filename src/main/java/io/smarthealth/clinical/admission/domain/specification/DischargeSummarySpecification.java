package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class DischargeSummarySpecification {

    public DischargeSummarySpecification() {
        super();
    }

    public static Specification<DischargeSummary> createSpecification(final String dischargeNo, String patientNo, final String term, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (dischargeNo != null) {
                predicates.add(cb.equal(root.get("dischargeNo"), dischargeNo));
            }
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("dischargeDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("doctor").get("fullName"), likeExpression),
                                cb.like(root.get("patient").get("patientNumber"), likeExpression),
                                cb.like(root.get("patient").get("fullName"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

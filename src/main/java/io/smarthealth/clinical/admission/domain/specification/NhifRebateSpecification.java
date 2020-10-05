package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.NhifRebate;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class NhifRebateSpecification {

    public NhifRebateSpecification() {
        super();
    }

    public static Specification<NhifRebate> createSpecification(final String admissionNo, final String patientNumber, String memberNumber, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (admissionNo != null) {
                predicates.add(cb.equal(root.get("admission").get("admissionNo"), admissionNo));
            }
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
            }
            if (memberNumber != null) {
                predicates.add(cb.equal(root.get("memberNumber"), memberNumber));
            }            
            if (range != null) {
                predicates.add(
                        cb.between(root.get("date"), range.getStartDateTime(), range.getEndDateTime())
                );
            }
    
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

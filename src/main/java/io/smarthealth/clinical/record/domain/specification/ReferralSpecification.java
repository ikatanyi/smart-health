package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.Referrals;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Simon.Waweru
 */
public class ReferralSpecification {

    public ReferralSpecification() {
        super();
    }

    public static Specification<Referrals> createSpecification(String visitNumber, String patientNumber) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
            }

            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

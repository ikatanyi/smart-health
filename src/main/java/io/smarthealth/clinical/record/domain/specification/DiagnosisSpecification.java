package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.clinical.laboratory.domain.specification.*;
import io.smarthealth.clinical.laboratory.domain.LabResult;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class DiagnosisSpecification {

    public DiagnosisSpecification() {
        super();
    }

    public static Specification<PatientDiagnosis> createSpecification(String visitNumber, String patientNumber, Gender gender, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
            }
            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }
            if (gender != null) {
                predicates.add(cb.equal(root.get("patient").get("gender"), gender));
            }
           
            if (range != null) {
                predicates.add(cb.between(root.get("date"), range.getStartDateTime(), range.getEndDateTime()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

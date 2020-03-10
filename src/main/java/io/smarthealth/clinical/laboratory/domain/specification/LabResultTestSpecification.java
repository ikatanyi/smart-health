package io.smarthealth.clinical.laboratory.domain.specification;

import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.ArrayList;
import static java.util.EnumSet.range;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class LabResultTestSpecification {

    public LabResultTestSpecification() {
        super();
    }

    public static Specification<LabRegisterTest> createSpecification(String visitNo, String labNumber) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visitNo != null) {
                predicates.add(cb.equal(root.get("labRegister").get("visit").get("visitNumber"), visitNo));
            }
//            if (patientNo != null) {
//                predicates.add(cb.equal(root.get("labRegister").get("visit").get("patient").get("patientNumber"), patientNo));
//            }
            if (labNumber != null) {
                predicates.add(cb.equal(root.get("labRegister").get("labNumber"), labNumber));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

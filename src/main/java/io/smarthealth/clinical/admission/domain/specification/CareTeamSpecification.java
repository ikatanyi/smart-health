package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.CareTeam;
import io.smarthealth.clinical.admission.domain.CareTeamRole;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class CareTeamSpecification {

    public CareTeamSpecification() {
        super();
    }

    public static Specification<CareTeam> createSpecification(final String patientNo, final String admissionNo, final CareTeamRole careRole, final Boolean active,  final Boolean voided) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (patientNo!=null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }
            if (admissionNo!=null) {
                predicates.add(cb.equal(root.get("admission").get("admissionNo"), admissionNo));
            }
            if (careRole!=null) {
                predicates.add(cb.equal(root.get("careRole"), careRole));
            }
            if(active!=null){
                predicates.add(cb.equal(root.get("isActive"), active));
            }
            if(voided!=null){
                predicates.add(cb.equal(root.get("voided"), voided));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

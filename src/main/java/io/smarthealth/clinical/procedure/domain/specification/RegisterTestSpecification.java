package io.smarthealth.clinical.procedure.domain.specification;

import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class RegisterTestSpecification {

    public RegisterTestSpecification() {
        super();
    }

    public static Specification<PatientProcedureTest> createSpecification(String PatientNumber,String scanNo, String visitId, ProcedureTestState status,Boolean isWalkin, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patientProcedureRegister").get("patientNo"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("patientProcedureRegister").get("accessNo"), scanNo));
            }
            if (isWalkin != null) {
                predicates.add(cb.equal(root.get("patientProcedureRegister").get("isWalkin"), isWalkin));
            }
            if (visitId != null) {
                predicates.add(cb.equal(root.get("patientProcedureRegister").get("visit").get("visitNumber"), visitId));
            }
            if(status!=null)
                predicates.add(cb.equal(root.get("status"), status));
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("procedureDate"), range.getStartDate(), range.getEndDate())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

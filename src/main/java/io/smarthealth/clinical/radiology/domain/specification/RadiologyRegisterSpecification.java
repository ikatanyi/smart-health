package io.smarthealth.clinical.radiology.domain.specification;

import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class RadiologyRegisterSpecification {

    public RadiologyRegisterSpecification() {
        super();
    }

    public static Specification<PatientScanRegister> createSpecification(String PatientNumber,String scanNo, String visitId, ScanTestState status, Boolean isWalkin, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patientNo"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("accessNo"), scanNo));
            }
            if (visitId != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitId));
            }
             if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
             if (isWalkin != null) {
                predicates.add(cb.equal(root.get("isWalkin"), isWalkin));
            } 
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("receivedDate"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

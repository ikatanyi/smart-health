package io.smarthealth.clinical.radiology.domain.specification;

import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class RadiologyResultSpecification {

    public RadiologyResultSpecification() {
        super();
    }

    public static Specification<RadiologyResult> createSpecification(String PatientNumber,String scanNo, String visitId, Boolean isWalkin, ScanTestState status, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("accessNo"), scanNo));
            }
            if (visitId != null) {
                predicates.add(cb.equal(root.get("patientScanRegister").get("visit").get("visitId"), visitId));
            }
            if (isWalkin != null) {
                predicates.add(cb.equal(root.get("patientScanRegister").get("isWalkin"), isWalkin));
            }
             if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("resultsDate"), range.getStartDate(), range.getEndDate())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

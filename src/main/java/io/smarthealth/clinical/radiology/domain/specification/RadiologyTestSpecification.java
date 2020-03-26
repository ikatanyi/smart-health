package io.smarthealth.clinical.radiology.domain.specification;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class RadiologyTestSpecification {

    public RadiologyTestSpecification() {
        super();
    }

    public static Specification<PatientScanTest> createSpecification(String PatientNumber,String scanNo, String visitId, Boolean isWalkin, DateRange range) {
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
             if(range!=null){
                  predicates.add(
                          
                     cb.between(root.get("patientScanRegister").get("receivedDate"), range.getStartDate(), range.getStartDate())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

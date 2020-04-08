package io.smarthealth.clinical.radiology.domain.specification;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
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
public class RadiologyTestSpecification {

    public RadiologyTestSpecification() {
        super();
    }

    public static Specification<PatientScanTest> createSpecification(String PatientNumber, String scanNo, String visitId, Boolean isWalkin, ScanTestState status, DateRange range, String search) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patientScanRegister").get("patientNumber"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("patientScanRegister").get("accessNo"), scanNo));
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
                          
                     cb.between(root.get("patientScanRegister").get("receivedDate"), range.getStartDate(), range.getStartDate())
                  );
              }
             if (search != null) {
                final String likeExpression = "%" + search + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("patientScanRegister").get("visit").get("visitNumber"), likeExpression),
                                cb.like(root.get("patientScanRegister").get("patientNo"), likeExpression),
                                cb.like(root.get("patientScanRegister").get("patientName"), likeExpression),
                                cb.like(root.get("patientScanRegister").get("visit").get("patient").get("fullName"), likeExpression),
                                cb.like(root.get("patientScanRegister").get("visit").get("patient").get("patientNumber"), likeExpression), //
                                cb.like(root.get("patientScanRegister").get("accessNo"), likeExpression)
                        )
                );
            } 
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

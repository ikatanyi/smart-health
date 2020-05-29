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

    public static Specification<RadiologyResult> createSpecification(String PatientNumber, String scanNo, String visitNumber, Boolean isWalkin, ScanTestState status, DateRange range, String search) {
        //System.out.println("To search "+search);
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patientScanTest").get("patientScanRegister").get("patientNo"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("patientScanTest").get("patientScanRegister").get("accessNo"), scanNo));
            }
            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("patientScanTest").get("patientScanRegister").get("visit").get("visitNumber"), visitNumber));
            }
            if (isWalkin != null) {
                predicates.add(cb.equal(root.get("patientScanTest").get("patientScanRegister").get("isWalkin"), isWalkin));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("resultsDate"), range.getStartDate(), range.getEndDate())
                );
            }

            if (search != null) {
                final String likeExpression = "%" + search + "%";
//                System.out.println("likeExpression "+likeExpression);
                predicates.add(
                        cb.or(
                                cb.like(root.get("patientScanTest").get("patientScanRegister").get("visit").get("visitNumber"), likeExpression),
                                cb.like(root.get("patientScanTest").get("patientScanRegister").get("patientNo"), likeExpression),
                                cb.like(root.get("patientScanTest").get("patientScanRegister").get("patientName"), likeExpression),
                                //cb.like(root.get("patientScanTest").get("patientScanRegister").get("visit").get("patient").get("fullName"), likeExpression),
                                cb.like(root.get("patientScanTest").get("patientScanRegister").get("visit").get("patient").get("patientNumber"), likeExpression), //
                                cb.like(root.get("patientScanTest").get("patientScanRegister").get("accessNo"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

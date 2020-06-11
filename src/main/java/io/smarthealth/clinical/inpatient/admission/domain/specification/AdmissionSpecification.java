package io.smarthealth.clinical.inpatient.admission.domain.specification;

import io.smarthealth.accounting.billing.data.BillSummary;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class AdmissionSpecification {

//    public static Specification<Admission> billHasBalance() {
//        return (Root<Admission> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
//            return cb.greaterThan(root.get("balance"), 0);
//        };
//    }
    public static Specification<Admission> createSpecification(String patientNo, String admissionNo, Admission.Status status, DateRange range) {
        return (Root<Admission> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patientBill").get("patient").get("patientNumber"), patientNo));
            }
            if (admissionNo != null) {
                predicates.add(cb.equal(root.get("admissionNo"), admissionNo));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("admissionDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

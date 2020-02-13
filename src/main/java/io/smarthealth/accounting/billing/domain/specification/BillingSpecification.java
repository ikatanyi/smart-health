package io.smarthealth.accounting.billing.domain.specification;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class BillingSpecification {

    public BillingSpecification() {
        super();
    }                  
    public static Specification<PatientBill> createSpecification(String refNo, String visitNo, String patientNo, String paymentMode, String billNo, BillStatus status, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (refNo != null) {
                predicates.add(cb.equal(root.get("transactionId"), refNo));
            }

            if (visitNo != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNo));
            }

            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }

            if (paymentMode != null) {
                predicates.add(cb.equal(root.get("paymentMode"), paymentMode));
            }

            if (billNo != null) {
                predicates.add(cb.equal(root.get("billNumber"), billNo));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if(range!=null){
                  predicates.add(
                     cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
                  );
              }

//            if (term != null) {
//                final String likeExpression = "%" + term + "%";
//                predicates.add(
//                        cb.or(
//                                cb.like(root.get("accountNumber"), likeExpression),
//                                cb.like(root.get("accountName"), likeExpression)
//                        )
//                );
//            }
//            if (type != null) {
//                predicates.add(cb.equal(root.get("accountType"), type));
//            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

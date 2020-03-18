package io.smarthealth.clinical.laboratory.domain.specification;

import io.smarthealth.clinical.laboratory.domain.LabRegister;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class LabRegisterSpecification {

    public LabRegisterSpecification() {
        super();
    }

    public static Specification<LabRegister> createSpecification(String labNumber, String orderNumber, String visitNumber, String patientNumber, LabTestStatus status,DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (orderNumber != null) {
                predicates.add(cb.equal(root.get("orderNumber"), orderNumber));
            }
            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("patient").get("patientNumber"), patientNumber));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
              if (range != null) {
                   System.out.println("date ranger .. "+range.getStartDateTime()+ " end : "+range.getEndDateTime());
                predicates.add(
                      
                        cb.between(root.get("requestDatetime"), range.getStartDateTime(), range.getEndDateTime())
                );
            }
//            if (service != null) {
//                final String likeExpression = "%" + service + "%";
//                predicates.add(
//                        cb.or(
//                                cb.like(root.get("serviceType").get("itemName"), likeExpression),
//                                cb.like(root.get("serviceType").get("itemCode"), likeExpression)
//                        )
//                );
//            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

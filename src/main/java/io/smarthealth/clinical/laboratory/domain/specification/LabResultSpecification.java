package io.smarthealth.clinical.laboratory.domain.specification;

import io.smarthealth.clinical.laboratory.domain.LabResult;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class LabResultSpecification {

    public LabResultSpecification() {
        super();
    }

    public static Specification<LabResult> createSpecification(String visitNumber, String patientNumber, String labNumber, Boolean walkin, String testName,String orderNumber, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("labRegisterTest").get("labRegister").get("visit").get("visitNumber"), visitNumber));
            }
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("patient").get("patientNumber"), patientNumber));
            }
            if (labNumber != null) {
                predicates.add(cb.equal(root.get("labRequestTest").get("labRequest").get("labNumber"), labNumber));
            }
            if (walkin != null) {
                predicates.add(cb.equal(root.get("labRequestTest").get("labRequest").get("isWalkin"), walkin));
            }
            if (orderNumber != null) {
                predicates.add(cb.equal(root.get("labRequestTest").get("labRequest").get("orderNumber"), orderNumber));
            }
            if (testName != null) {
                final String likeExpression = "%" + testName + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("labRequestTest").get("labTest").get("testName"), likeExpression),
                                cb.like(root.get("labRequestTest").get("labTest").get("code"), likeExpression)
                        )
                );
            } 
            if (visitNumber==null && range != null) {
                predicates.add(
                        cb.between(root.get("resultsDate"), range.getStartDateTime(), range.getEndDateTime())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

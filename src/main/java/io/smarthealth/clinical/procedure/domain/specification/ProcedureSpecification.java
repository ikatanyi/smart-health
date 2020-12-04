package io.smarthealth.clinical.procedure.domain.specification;

import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.procedure.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.procedure.domain.ProcedureConfiguration;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class ProcedureSpecification {

    public ProcedureSpecification() {
        super();
    }

    public static Specification<PatientProcedureRegister> createSpecification(String PatientNumber, String scanNo, String visitId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), PatientNumber));
            }
            if (scanNo != null) {
                predicates.add(cb.equal(root.get("accessNo"), scanNo));
            }
            if (visitId != null) {
                predicates.add(cb.greaterThan(root.get("visit").get("visitId"), visitId));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("createdOn"), range.getStartDateTime(), range.getEndDateTime())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<ProcedureConfiguration> createConfigSpecification(String itemCode, String term, Boolean isPercentage, BigDecimal valueAmount, FeeCategory feeCategory) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (isPercentage != null) {
                predicates.add(cb.equal(root.get("isPercentage"), isPercentage));
            }
            if (valueAmount != null) {
                predicates.add(cb.equal(root.get("valueAmount"), valueAmount));
            }
            if (feeCategory != null) {
                predicates.add(cb.equal(root.get("feeCategory"), feeCategory));
            }

            if (itemCode != null) {
                predicates.add(cb.equal(root.get("procedure").get("itemCode"), itemCode));
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("itemName"), likeExpression),
                                cb.like(root.get("itemCode"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

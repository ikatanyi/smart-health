package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class RequisitionSpecification {

    public RequisitionSpecification() {
        super();
    }

    public static Specification<Requisition> createSpecification(List<RequisitionStatus> status, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDate(), range.getEndDate())
                );
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(root.get("status").in(status));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

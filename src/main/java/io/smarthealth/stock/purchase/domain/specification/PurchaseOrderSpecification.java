/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class PurchaseOrderSpecification {

    public PurchaseOrderSpecification() {
    }

    public static Specification<PurchaseOrder> createSpecification(Long supplierId, List<PurchaseOrderStatus> status, String search, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (supplierId != null) {
                predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(root.get("status").in(status));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDate(), range.getEndDate())
                );
            }
            if (search != null) {
                final String likeExpression = "%" + search + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("supplier").get("supplierName"), likeExpression), //
                                cb.like(root.get("orderNumber"), likeExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

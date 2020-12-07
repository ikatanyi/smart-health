/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.domain.specification;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class TheatreFeeSpecification {

    public static Specification<TheatreFee> createConfigSpecification(String itemCode, String term, Boolean isPercentage, BigDecimal valueAmount, FeeCategory feeCategory) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (isPercentage != null) {
                predicates.add(cb.equal(root.get("isPercentage"), isPercentage));
            }
            if (valueAmount != null) {
                predicates.add(cb.equal(root.get("amount"), valueAmount));
            }
            if (feeCategory != null) {
                predicates.add(cb.equal(root.get("feeCategory"), feeCategory));
            }

            if (itemCode != null) {
                predicates.add(cb.equal(root.get("serviceType").get("itemCode"), itemCode));
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("serviceType").get("itemName"), likeExpression),
                                cb.like(root.get("serviceType").get("itemCode"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

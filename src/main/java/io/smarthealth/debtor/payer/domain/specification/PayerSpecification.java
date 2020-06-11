/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.domain.specification;

import io.smarthealth.debtor.payer.domain.Payer;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author simz
 */
public class PayerSpecification {

    private PayerSpecification() {
        super();
    }

    public static Specification<Payer> createPayerSpecification(final String term) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (term != null) {
                final String termExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("payerName"), termExpression),
                                cb.like(root.get("legalName"), termExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain.specification;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author simz
 */
public class SchemeSpecification {

    private SchemeSpecification() {
        super();
    }

    public static Specification<Scheme> createSchemeSpecification(final Payer payer, final String term) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payer != null) {
                predicates.add(cb.equal(root.get("payer"), payer));
            }
          
            if (term != null) {
                final String termExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("schemeName"), termExpression),
                                cb.like(root.get("schemeCode"), termExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

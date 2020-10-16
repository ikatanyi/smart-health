/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain.specification;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author kennedy.ikatanyi
 */
public class SchemeConfigSpecification {

    private SchemeConfigSpecification() {
        super();
    }

    public static Specification<SchemeConfigurations> createSchemeSpecification(final Long payerId, final String term, Boolean withCopay, Boolean smartEnabled) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (payerId != null) {
                predicates.add(cb.equal(root.get("scheme").get("payer").get("id"), payerId));
            }
            if(withCopay)
                predicates.add(cb.notEqual(root.get("coPayValue"), 0));
            if(smartEnabled!=null)
                predicates.add(cb.equal(root.get("smartEnabled"), smartEnabled));
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

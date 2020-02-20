/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data.specification;

import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Simon.waweru
 */
public class WalkingSpecification {

    public static Specification<WalkIn> createSpecification(final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("firstName"), likeExpression),
                                cb.like(root.get("secondName"), likeExpression),
                                cb.like(root.get("surname"), likeExpression),
                                cb.like(root.get("walkingIdentitificationNo"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}

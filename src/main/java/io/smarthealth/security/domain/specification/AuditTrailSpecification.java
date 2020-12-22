/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.AuditTrail;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author kent
 */
public class AuditTrailSpecification {
    private AuditTrailSpecification() {
        super();
    }

    public static Specification<AuditTrail> createSpecification(DateRange range, String name) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();
            
            if (range != null) {
                predicates.add(
                        cb.between(root.get("createdOn"), range.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant(), range.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
            }

            if (name != null) {
                final String likeExpression = "%" + name + "%";
                predicates.add( cb.like(root.get("name"), likeExpression));
            } 
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentificationType;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 *
 * @author Simon.waweru
 */
public class PatientIdentifierSpecification {

    public static Specification<PatientIdentifier> createSpecification(PatientIdentificationType type, String value) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(
                        cb.equal(root.get("type"), type)
                );
            }
            if (value != null) {
                final String likeExpression = "%" + value + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("value"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}

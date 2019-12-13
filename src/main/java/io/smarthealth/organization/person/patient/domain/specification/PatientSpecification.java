/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain.specification;

import io.smarthealth.organization.person.patient.domain.Patient;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 *
 * @author Simon.waweru
 */
public class PatientSpecification {

    public static Specification<Patient> createSpecification(final String term) {
        return (root, query, cb) -> {
            Patient patient = new Patient();
            patient.getGivenName();
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("givenName"), likeExpression),
                                cb.like(root.get("middleName"), likeExpression),
                                cb.like(root.get("surname"), likeExpression),
                                cb.like(root.get("patientNumber"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}

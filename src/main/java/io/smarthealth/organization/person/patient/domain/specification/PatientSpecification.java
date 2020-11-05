/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Simon.waweru
 */
public class PatientSpecification {

    public static Specification<Patient> createSpecification(final DateRange range, final String term) {
        return (root, query, cb) -> {
            Patient patient = new Patient();
            patient.getGivenName();
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (range != null) {
                predicates.add(
                        cb.between(root.get("dateRegistered"), range.getStartDate(), range.getEndDate())
                );
            }
            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
//                                cb.like(root.get("fullName"), likeExpression),
                                cb.like(root.get("givenName"), likeExpression),
                                cb.like(root.get("middleName"), likeExpression),
                                cb.like(root.get("surname"), likeExpression),
                                cb.like(root.get("patientNumber"), likeExpression),
                                cb.like(root.get("primaryContact"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}

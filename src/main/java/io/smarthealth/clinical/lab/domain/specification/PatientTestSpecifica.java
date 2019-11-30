/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Simon.Waweru
 */
public class PatientTestSpecifica {

    public PatientTestSpecifica() {
        super();
    }

    public static Specification<PatientTestRegister> createSpecification(final String visitNumber, final LabTestState status/*, Date from , Date to*/) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
//            if (from != null && to!=null) {
//                predicates.add(
//                        cb.between(root.get("createdOn"), from, to));
//            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

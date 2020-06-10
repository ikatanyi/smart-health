/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.domain.specification;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author simz
 */
public class PatientQueueSpecification {

    private PatientQueueSpecification() {
        super();
    }

    public static Specification<PatientQueue> createSpecification(
            Visit visit, Employee employee, ServicePoint servicePoint, Patient patient) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visit != null) {
                predicates.add(cb.equal(root.get("visit"), visit));
            }
            if (employee != null) {
                predicates.add(cb.equal(root.get("staffNumber"), employee));
            }
            if (servicePoint != null) {
                predicates.add(cb.equal(root.get("servicePoint"), servicePoint));
            }
            if (patient != null) {
                predicates.add(cb.equal(root.get("patient"), patient));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

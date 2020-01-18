/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain.specification;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
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
public class VisitSpecification {

    private VisitSpecification() {
        super();
    }

    public static Specification<Visit> createSpecification(Visit visit, Employee employee, ServicePoint servicePoint, Patient patient, boolean visitIsRunning) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visit != null) {
                predicates.add(cb.equal(root.get("visit"), visit));
            }

            if (employee != null) {
                predicates.add(cb.equal(root.get("healthProvider"), employee));
            }
            if (servicePoint != null) {
                predicates.add(cb.equal(root.get("servicePoint"), servicePoint));
            }
            if (patient != null) {
                predicates.add(cb.equal(root.get("patient"), patient));
            }

            if (visitIsRunning) {
                predicates.add(
                        cb.or(
                                cb.equal(root.get("status"), VisitEnum.Status.Admitted),
                                cb.equal(root.get("status"), VisitEnum.Status.CheckIn)
                        )
                );
            }

            if (!visitIsRunning) {
                predicates.add(
                        cb.or(
                                cb.equal(root.get("status"), VisitEnum.Status.Discharged),
                                cb.equal(root.get("status"), VisitEnum.Status.CheckOut),
                                cb.equal(root.get("status"), VisitEnum.Status.Transferred)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

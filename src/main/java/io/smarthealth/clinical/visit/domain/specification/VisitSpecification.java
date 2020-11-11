/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain.specification;

import io.smarthealth.administration.servicepoint.domain.ServicePoints;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.security.domain.User;
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

    public static Specification<Visit> createSpecification(String visitNumber, Employee employee, ServicePoints servicePoint, Patient patient, String patientName, boolean visitIsRunning, DateRange dateRange, final Boolean isActiveOnConsultation, final boolean orderByTriageCategory, final String queryTerm) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (visitNumber != null) {
                final String visitNumberExpression = "%" + visitNumber + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("visitNumber"), visitNumberExpression)
                        )
                );
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
//                                cb.equal(root.get("status"), VisitEnum.Status.Transferred)
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

            if (dateRange != null) {
                predicates.add(
                        cb.between(
                                root.get("startDatetime"), dateRange.getStartDateTime(), dateRange.getEndDateTime()
                        )
                );
            }

            if (patientName != null) {

                final String patientNameExpression = "%" + patientName + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("patient").get("givenName"), patientNameExpression),
                                cb.like(root.get("patient").get("middleName"), patientNameExpression),
                                cb.like(root.get("patient").get("surname"), patientNameExpression)
                        )
                );
            }

            if (queryTerm != null) {

                final String term = "%" + queryTerm + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("visitNumber"), term),
                                cb.like(root.get("patient").get("patientNumber"), term),
                                cb.like(root.get("patient").get("givenName"), term),
                                cb.like(root.get("patient").get("middleName"), term),
                                cb.like(root.get("patient").get("surname"), term)
                        )
                );
            }

            if (isActiveOnConsultation != null) {
                predicates.add(cb.equal(root.get("isActiveOnConsultation"), isActiveOnConsultation));
            }
            if (orderByTriageCategory) {
                query.orderBy(cb.asc(root.get("triageCategory")), cb.asc(root.get("startDatetime")));
            }
            if (!orderByTriageCategory) {
                query.orderBy(cb.desc(root.get("startDatetime")), cb.desc(root.get("triageCategory")));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import java.util.Arrays;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class DoctorRequestSpecification {

    public DoctorRequestSpecification() {
        super();
    }

    public static Specification<DoctorRequest> createSpecification(final String visitNumber, final String patientNumber, final RequestType requestType, final FullFillerStatusType fulfillerStatus, String groupBy, Boolean activeVisit, final String term, final DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (activeVisit != null) {
                if (activeVisit) {
                    predicates.add(root.get("visit").get("status").in(Arrays.asList(VisitEnum.Status.CheckIn, VisitEnum.Status.Admitted)));
                } else {
                    predicates.add(root.get("visit").get("status").in(Arrays.asList(VisitEnum.Status.CheckOut, VisitEnum.Status.Discharged, VisitEnum.Status.Transferred)));
                }
            }

            if (fulfillerStatus != null) {
                predicates.add(cb.equal(root.get("fulfillerStatus"), fulfillerStatus));
            }

            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }

            if (groupBy != null) {
                query.groupBy(root.get("patient"));
            }

            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }

            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("visit").get("visitNumber"), likeExpression),
                                cb.like(root.get("orderNumber"), likeExpression),
                                cb.like(root.get("patient").get("patientNumber"), likeExpression),
                                cb.like(root.get("patient").get("fullName"), likeExpression)
                        )
                );
            }

            if (range != null) {
                System.out.println("range " + range.toString());
                predicates.add(
                        cb.between(root.get("orderDate"), range.getStartDate(), range.getEndDate())
                // cb.between(root.get("visit").get("startDatetime"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<DoctorRequest> unfullfilledRequests(RequestType requestType) {

        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("visit").get("status"), VisitEnum.Status.CheckIn));
            predicates.add(cb.equal(root.get("fulfillerStatus"), FullFillerStatusType.Unfulfilled));
            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }
            query.groupBy(root.get("patient"));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    //final Patient patient, final FullFillerStatusType fullfillerStatus, final RequestType requestType
    public static Specification<DoctorRequest> unfullfilledRequests(String patientNumber, RequestType requestType) {

        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
            }
            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }

            predicates.add(cb.equal(root.get("fulfillerStatus"), FullFillerStatusType.Unfulfilled));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

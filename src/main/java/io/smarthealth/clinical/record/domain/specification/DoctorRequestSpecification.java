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

    public static Specification<DoctorRequest> createSpecification(final String visitNumber, final String patientNumber, final RequestType requestType, final FullFillerStatusType fulfillerStatus/*, Date from , Date to*/, String groupBy, Boolean activeVisit) {
//        System.out.println("visitNumber to request " + visitNumber);
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (activeVisit != null) {
                if (activeVisit) {
                    predicates.add(root.get("visit").get("status").in(Arrays.asList(VisitEnum.Status.CheckIn, VisitEnum.Status.Admitted)));
                } else {
                    predicates.add(root.get("visit").get("status").in(Arrays.asList(VisitEnum.Status.CheckOut, VisitEnum.Status.Discharged)));
                }
            }
            if (visitNumber != null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }

            if (patientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNumber));
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

//            if (from != null && to!=null) {
//                predicates.add(
//                        cb.between(root.get("createdOn"), from, to));
//            }
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

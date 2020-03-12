/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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

    public static Specification<DoctorRequest> createSpecification(final String visitNumber, final String patientNumber, final RequestType requestType, final String fulfillerStatus) {
        System.out.println("visitNumber to request " + visitNumber);
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

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

//            if (groupBy != null) {
//                query.groupBy(root.get("patient"));
//            }

//            if (from != null && to!=null) {
//                predicates.add(
//                        cb.between(root.get("createdOn"), from, to));
//            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

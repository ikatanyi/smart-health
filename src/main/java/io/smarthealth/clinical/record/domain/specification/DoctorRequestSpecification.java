/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain.specification;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import java.util.ArrayList;
import java.util.Date;
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

    public static Specification<DoctorRequest> createSpecification(final String visitNumber, final String status, final String requestType, Date from , Date to) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            
            if (visitNumber!=null) {
                predicates.add(cb.equal(root.get("visit").get("visitNumber"), visitNumber));
            }

            if (status!=null) {
                predicates.add(cb.equal(root.get("fulfillerStatus"), status));
            }

            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }
            
            if (from != null && to!=null) {
                predicates.add(
                        cb.between(root.get("createdOn"), from, to));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

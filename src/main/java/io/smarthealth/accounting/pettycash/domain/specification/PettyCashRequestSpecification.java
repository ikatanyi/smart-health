/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain.specification;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Simon.waweru
 */
public class PettyCashRequestSpecification {

    private PettyCashRequestSpecification() {
        super();
    }

    public static Specification<PettyCashRequests> createSpecification(final String requestNo, final Employee employee, final PettyCashStatus status, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (requestNo != null) {
                predicates.add(cb.equal(root.get("requestNo"), requestNo));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (employee != null) {
                predicates.add(cb.equal(root.get("requestedBy"), employee));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("createdOn").as(LocalDate.class), range.getStartDate(), range.getEndDate())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

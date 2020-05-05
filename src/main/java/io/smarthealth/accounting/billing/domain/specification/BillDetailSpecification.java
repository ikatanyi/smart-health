/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain.specification;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class BillDetailSpecification {

    public static Specification<PatientBillItem> getBillItemByVisitNumber(String visitNo) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.FALSE));
            if (visitNo != null) {
                predicates.add(cb.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNo));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<PatientBillItem> getBillItemByWalkinNumber(String walkIn) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.TRUE));

            if (walkIn != null) {
                predicates.add(cb.equal(root.get("patientBill").get("reference"), walkIn));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

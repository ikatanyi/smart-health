/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain.specification;

import io.smarthealth.accounting.billing.data.BillSummary;
import io.smarthealth.accounting.billing.domain.PatientBill;
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
public class PatientBillSpecification {

    public static Specification<PatientBill> billHasBalance() {
        return (Root<PatientBill> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.greaterThan(root.get("balance"), 0);
        };
    }

    public static Specification<PatientBillItem> createSpecification(String patientNo, String visitNo, String billNumber, String transactionId, Long servicePointId, Boolean hasBalance, BillStatus status, DateRange range) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.FALSE));
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patientBill").get("patient").get("patientNumber"), patientNo));
            }
            if (visitNo != null) {
                predicates.add(cb.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNo));
            }
            if (billNumber != null) {
                predicates.add(cb.equal(root.get("patientBill").get("billNumber"), billNumber));
            }
            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transactionId"), transactionId));
            }
            if (servicePointId != null) {
                predicates.add(cb.equal(root.get("servicePointId"), servicePointId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (hasBalance != null && hasBalance) {
                predicates.add(cb.greaterThan(root.get("balance"), 0));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<PatientBillItem> getWalkinBillItems(String walkIn, Boolean hasBalance) {
        return (Root<PatientBillItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), Boolean.TRUE));
            
            if (walkIn != null) {
                predicates.add(cb.equal(root.get("patientBill").get("reference"), walkIn));
            }

            if (hasBalance != null && hasBalance) {
                predicates.add(cb.greaterThan(root.get("balance"), 0));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<BillSummary> billSummary(String patientNo, String visitNo, Boolean hasBalance, DateRange range) {
        return (Root<BillSummary> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patientNumber"), patientNo));
            }
            if (visitNo != null) {
                predicates.add(cb.equal(root.get("visitNumber"), visitNo));
            }
            if (hasBalance != null && hasBalance) {
                predicates.add(cb.greaterThan(root.get("balance"), 0));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

// [   public String findTeacherByStudentName(String name) {
//    List<Teacher> list = teacherRepository.findAll(new Specification<Teacher>() {
//        @Override
//        public Predicate toPredicate(Root<Teacher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//            Predicate predicate = cb.conjunction();
//            // Create a subquery and specify a subquery entity object
//            Subquery<Student> subQuery = query.subquery(Student.class);
//            Root<Student> subRoot = subQuery.from(Student.class);
//            // Query the teacher ID corresponding to the student through the connection
//            Join<Student, Teacher> join = subRoot.join("teachers", JoinType.LEFT);
//            // Set the query field of the subquery,
//            // is equivalent to select teacher.id
//            // To query all, ie select * , set subQuer.select(subRoot);
//            subQuery.select(join.get("id"));
//            / / Create a query condition, student name = name
//            Predicate subPredicate = cb.equal(subRoot.get("name"), name);
//            // subquery condition
//            subQuery.where(subPredicate);
//            / / Add the condition of the subquery result to the query, add to the parent query
//            Expression<String> exp = root.get("id");
//            Predicate p1 = exp.in(subQuery);
//            predicate.getExpressions().add(p1);
//            return predicate;
//        }
//    });
//    StringBuilder teacherInfo = new StringBuilder("");
//    list.forEach(a -> {System.out.println(a.toString()); teacherInfo.append(a.toString()).append("\r\n");});
//    return teacherInfo.toString();
//}
}

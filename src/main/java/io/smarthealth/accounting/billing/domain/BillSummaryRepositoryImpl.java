package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Repository
@Transactional(readOnly = true)
public class BillSummaryRepositoryImpl implements BillSummaryRepository {

    EntityManager em;

    public BillSummaryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<SummaryBill> getBillSummary(String visitNumber, String patientNumber, Boolean hasBalance, DateRange range, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SummaryBill> cq = cb.createQuery(SummaryBill.class);
        Root<PatientBillItem> root = cq.from(PatientBillItem.class);
        cq.multiselect(
                root.get("patientBill").get("billingDate"),
                root.get("patientBill").get("visit").get("visitNumber"),
                root.get("patientBill").get("patient").get("patientNumber"),
                root.get("patientBill").get("patient").get("fullName"),
                cb.sum(root.get("amount")).as(BigDecimal.class),
                cb.sum(root.get("balance")).as(BigDecimal.class),
                root.get("patientBill").get("paymentMode"),
                root.get("patientBill").get("walkinFlag")
        );

        List<Predicate> predicates = new ArrayList<>();

        if (patientNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("patient").get("patientNumber"), patientNumber));
        }
        if (visitNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNumber));
        }
        if (range != null) {
            predicates.add(
                    cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
            );
        }

        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get("patientBill").get("visit").get("visitNumber"));

        if (hasBalance != null) {
            if (hasBalance) {
                cq.having(cb.greaterThan(cb.sum(root.get("balance")), 0));
            } else {
                cq.having(cb.lessThanOrEqualTo(cb.sum(root.get("balance")), 0));
            }
        }

        List<SummaryBill> result = em.createQuery(cq).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PatientBillItem> rootCount = countQuery.from(PatientBillItem.class);
        countQuery.select(cb.count(rootCount)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        Long count = em.createQuery(countQuery).getSingleResult();
        Page<SummaryBill> result1 = new PageImpl<>(result, pageable, count);
        return result1;
    }

    @Override
    public Page<SummaryBill> getWalkinBillSummary(String patientNumber, Boolean hasBalance, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SummaryBill> cq = cb.createQuery(SummaryBill.class);
        Root<PatientBillItem> root = cq.from(PatientBillItem.class);
        cq.multiselect(
                root.get("patientBill").get("billingDate"),
                root.get("patientBill").get("reference"),
                root.get("patientBill").get("reference"),
                root.get("patientBill").get("otherDetails"),
                cb.sum(root.get("amount")).as(BigDecimal.class),
                cb.sum(root.get("balance")).as(BigDecimal.class),
                root.get("patientBill").get("paymentMode"),
                root.get("patientBill").get("walkinFlag")
        );

        List<Predicate> predicates = new ArrayList<>();

        if (patientNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("reference"), patientNumber));
        }
//        if (range != null) {
//            predicates.add(
//                    cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
//            );
//        }

        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get("patientBill").get("reference"));

        if (hasBalance != null) {
            if (hasBalance) {
                cq.having(cb.greaterThan(cb.sum(root.get("balance")), 0));
            } else {
                cq.having(cb.lessThanOrEqualTo(cb.sum(root.get("balance")), 0));
            }
        }

        List<SummaryBill> result = em.createQuery(cq).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PatientBillItem> rootCount = countQuery.from(PatientBillItem.class);
        countQuery.select(cb.count(rootCount)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        Long count = em.createQuery(countQuery).getSingleResult();
        Page<SummaryBill> result1 = new PageImpl<>(result, pageable, count);
        return result1;
    }
}

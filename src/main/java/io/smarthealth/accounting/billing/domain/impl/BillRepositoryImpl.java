package io.smarthealth.accounting.billing.domain.impl;

import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.domain.BillRepository;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Repository
@Transactional(readOnly = true)
public class BillRepositoryImpl implements BillRepository {

    EntityManager em;

    public BillRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<SummaryBill> getBillSummary(String visitNumber, String patientNumber, Boolean hasBalance, Boolean isWalkin, PaymentMethod paymentMode, DateRange range, Boolean includeCanceled) {
        if (isWalkin != null && isWalkin) {
            return getWalkIn(patientNumber, hasBalance, paymentMode, range, includeCanceled);
        }
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
                root.get("patientBill").get("walkinFlag"),
                root.get("scheme").get("schemeName"),
                root.get("patientBill").get("visit").get("visitType")
        );

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), false));

        if (patientNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("patient").get("patientNumber"), patientNumber));
        }
        if (visitNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("visit").get("visitNumber"), visitNumber));
        }
        if (paymentMode != null) {
            predicates.add(cb.equal(root.get("patientBill").get("paymentMode"), paymentMode.name()));
        }
        if (includeCanceled != null && !includeCanceled) {
            predicates.add(cb.notEqual(root.get("status"), BillStatus.Canceled));
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

        List<SummaryBill> result = em.createQuery(cq).getResultList();

        if (isWalkin == null) {
            if (patientNumber == null) {
                patientNumber = visitNumber;
            }

            List<SummaryBill> walkin = getWalkIn(patientNumber, hasBalance, paymentMode, range, includeCanceled);
            result.addAll(walkin);
//            result.retainAll(walkin);

//            List<User> sortedUsers = users
//        .stream()
//        .sorted(Comparator.comparing(User::getScore))
//        .collect(Collectors.toList());
        }

        List<SummaryBill> sortedBills = result
                .stream()
                .sorted(Comparator.comparing(SummaryBill::getDate))
                .collect(Collectors.toList());

        return sortedBills;
    }

    @Override
    public BigDecimal getBillTotal(String visitNumber, Boolean includeCanceled) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> query = cb.createQuery(Double.class);
        Root<PatientBillItem> patientBillItem = query.from(PatientBillItem.class);
        query.select(cb.sum(patientBillItem.get("balance")));
        List<Predicate> predicates = new ArrayList<>();
        if (visitNumber != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("visitNumber"), visitNumber));
        }
        if (includeCanceled != null && !includeCanceled) {
            predicates.add(cb.notEqual(patientBillItem.get("status"), BillStatus.Canceled));
        }
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Double> typedQuery = em.createQuery(query);
        Double sum = typedQuery.getSingleResult();
        return BigDecimal.valueOf(sum);
    }

    private List<SummaryBill> getWalkIn(String patientNumber, Boolean hasBalance, PaymentMethod paymentMode, DateRange range, Boolean includeCanceled) {
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
                root.get("patientBill").get("walkinFlag"),
                root.get("scheme").get("schemeName"),
                root.get("patientBill").get("visit").get("visitType")
        );

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("patientBill").get("walkinFlag"), true));

        if (patientNumber != null) {
            predicates.add(cb.equal(root.get("patientBill").get("reference"), patientNumber));
        }
        if (paymentMode != null) {
            predicates.add(cb.equal(root.get("patientBill").get("paymentMode"), paymentMode.name()));
        }
        if (includeCanceled != null && !includeCanceled) {
            predicates.add(cb.notEqual(root.get("status"), BillStatus.Canceled));
        }
        if (range != null) {
            predicates.add(
                    cb.between(root.get("billingDate"), range.getStartDate(), range.getEndDate())
            );
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get("patientBill").get("reference"));

        if (hasBalance != null) {
            if (hasBalance) {
                cq.having(cb.greaterThan(cb.sum(root.get("balance")), 0));
            } else {
                cq.having(cb.lessThanOrEqualTo(cb.sum(root.get("balance")), 0));
            }
        }

        List<SummaryBill> result = em.createQuery(cq).getResultList();

        return result;
    }

    //can I select the items independently so that I can have fully control
}

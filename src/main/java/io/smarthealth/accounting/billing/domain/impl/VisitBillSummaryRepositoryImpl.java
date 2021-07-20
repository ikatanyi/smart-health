package io.smarthealth.accounting.billing.domain.impl;

import io.smarthealth.accounting.billing.data.VisitBillSummary;
import io.smarthealth.accounting.billing.domain.BillingQuery;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.VisitBillSummaryRepository;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.lang.DateRange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class VisitBillSummaryRepositoryImpl implements VisitBillSummaryRepository {

    EntityManager em;

    public VisitBillSummaryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression.toLowerCase());
    }

    @Override
    public Page<VisitBillSummary> getVisitBill(BillingQuery query) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<VisitBillSummary> cq = builder.createQuery(VisitBillSummary.class);

        Root<PatientBillItem> root = cq.from(PatientBillItem.class);

        Join<PatientBillItem, PaymentDetails> paymentDetailsJoin = root.join("patientBill").join("visit").join("paymentDetails", JoinType.LEFT);
        Join<PaymentDetails, Payer> payerJoin = paymentDetailsJoin.join("payer", JoinType.LEFT);
        Join<PaymentDetails, Scheme> schemeJoin = paymentDetailsJoin.join("scheme", JoinType.LEFT);

        Predicate isDebit = builder.equal(root.get("entryType"), BillEntryType.Debit);
        Predicate isCredit = builder.equal(root.get("entryType"), BillEntryType.Credit);

        Expression<BigDecimal> debitExpr = builder.selectCase()
                .when(isDebit, root.get("amount").as(BigDecimal.class))
                .otherwise(builder.literal(BigDecimal.ZERO)).as(BigDecimal.class);

        Expression<BigDecimal> creditExpr = builder.selectCase()
                .when(isCredit, root.get("amount").as(BigDecimal.class))
                .otherwise(builder.literal(BigDecimal.ZERO)).as(BigDecimal.class);

//        Expression<BigDecimal> balanceExpr = builder.diff(debitExpr, creditExpr);

        cq.multiselect(
                root.get("patientBill").get("visit").get("startDatetime").as(LocalDateTime.class),
                root.get("patientBill").get("visit").get("stopDatetime").as(LocalDateTime.class),
                root.get("patientBill").get("visit").get("visitNumber"),
                root.get("patientBill").get("visit").get("visitType"),
                root.get("patientBill").get("visit").get("paymentMethod"),
                root.get("patientBill").get("visit").get("status"),
                root.get("patientBill").get("patient").get("patientNumber"),
                root.get("patientBill").get("patient").get("fullName"),
                builder.sum(debitExpr).as(BigDecimal.class),
                builder.sum(creditExpr).as(BigDecimal.class),
//                builder.sum(balanceExpr).as(BigDecimal.class),
                payerJoin.get("id"),
                payerJoin.get("payerName"),
                schemeJoin.get("id"),
                schemeJoin.get("schemeName"),
                paymentDetailsJoin.get("memberName"),
                paymentDetailsJoin.get("policyNo"),
                paymentDetailsJoin.get("coPayValue"),
                paymentDetailsJoin.get("coPayCalcMethod"),
                paymentDetailsJoin.get("hasCapitation"),
                paymentDetailsJoin.get("capitationAmount")
        );

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get("patientBill").get("walkinFlag"), false));
        predicates.add(builder.notEqual(root.get("status"), BillStatus.Canceled));

        if (query.getPatientNumber() != null) {
            predicates.add(builder.equal(root.get("patientBill").get("patient").get("patientNumber"), query.getPatientNumber()));
        }
        if (query.getVisitType() != null) {
            predicates.add(builder.equal(root.get("patientBill").get("visit").get("visitType"), query.getVisitType()));
        }
        if (query.getPaymentMethod() != null) {
            predicates.add(builder.equal(root.get("patientBill").get("visit").get("paymentMethod"), query.getPaymentMethod()));
        }

        if (query.getVisitNumber() != null) {
            predicates.add(builder.equal(root.get("patientBill").get("visit").get("visitNumber"), query.getVisitNumber()));
        }
        if (StringUtils.isNotBlank(query.getSearch())) {
            predicates.add(
                    builder.or(
//                            builder.like(root.get("patientBill").get("visit").get("visitNumber"), contains(query.getSearch())),
                            builder.like(builder.lower(root.get("patientBill").get("patient").get("patientNumber")), contains(query.getSearch())),
                            builder.like(builder.lower(root.get("patientBill").get("patient").get("fullName")), contains(query.getSearch()))
                    )
            );

        }
        DateRange range = query.getDateRange();
        if (range != null) {
            predicates.add(
                    builder.between(root.get("patientBill").get("visit").get("startDatetime"), range.getStartDateTime(), range.getEndDateTime())
            );
        }

        cq.where(predicates.toArray(new Predicate[predicates.size()]))
                .groupBy(root.get("patientBill").get("visit").get("visitNumber"));

        Long count = getAllCount(query, builder);

        TypedQuery q = em.createQuery(cq);
        Pageable pageable = query.getPageable();
        if (!pageable.isUnpaged()) {
            q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            q.setMaxResults(pageable.getPageSize());
        }

        return new PageImpl<>(q.getResultList(), pageable, count);
    }

    private Long getAllCount(BillingQuery query, CriteriaBuilder builder) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<PatientBillItem> visitRootCount = countQuery.from(PatientBillItem.class);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(builder.equal(visitRootCount.get("patientBill").get("walkinFlag"), false));
        countPredicates.add(builder.notEqual(visitRootCount.get("status"), BillStatus.Canceled));
        if (query.getPatientNumber() != null) {
            countPredicates.add(builder.equal(visitRootCount.get("patientBill").get("patient").get("patientNumber"), query.getPatientNumber()));
        }
        if (query.getVisitType() != null) {
            countPredicates.add(builder.equal(visitRootCount.get("patientBill").get("visit").get("visitType"), query.getVisitType()));
        }
        if (query.getPaymentMethod() != null) {
            countPredicates.add(builder.equal(visitRootCount.get("patientBill").get("visit").get("paymentMethod"), query.getPaymentMethod()));
        }
        DateRange range = query.getDateRange();
        if (range != null) {
            countPredicates.add(
                    builder.between(visitRootCount.get("patientBill").get("visit").get("startDatetime"), range.getStartDateTime(), range.getEndDateTime())
            );
        }
        if (query.getSearch()!=null) {
            countPredicates.add(
                    builder.or(
                          builder.like(builder.lower(visitRootCount.get("patientBill").get("patient").get("patientNumber")), contains(query.getSearch())),
                            builder.like(builder.lower(visitRootCount.get("patientBill").get("patient").get("fullName")), contains(query.getSearch()))
                    )
            );
        }

        countQuery.select(builder.countDistinct(visitRootCount.get("patientBill").get("visit").get("visitNumber")))
                .where(builder.and(countPredicates.toArray(new Predicate[countPredicates.size()])));

        return em.createQuery(countQuery).getSingleResult();
    }
}

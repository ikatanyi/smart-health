package io.smarthealth.accounting.billing.domain.impl;

import io.smarthealth.accounting.billing.data.PatientBillDetail;
import io.smarthealth.accounting.billing.domain.CustomPatientBillRepository;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CustomPatientBillRepositoryImpl implements CustomPatientBillRepository {

    EntityManager em;

    public CustomPatientBillRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

    @Override
    public List<PatientBillDetail> getPatientBills(String search, String patientNumber, String visitNumber, PaymentMethod paymentMethod, DateRange range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PatientBillDetail> cq = cb.createQuery(PatientBillDetail.class);
        Root<PatientBillItem> patientBillItem = cq.from(PatientBillItem.class);

        Expression<BigDecimal> totalBillExpr = cb.selectCase()
                .when(cb.equal(patientBillItem.get("entryType"), BillEntryType.Debit), patientBillItem.get("amount").as(BigDecimal.class))
                .otherwise(cb.literal(BigDecimal.ZERO)).as(BigDecimal.class);

        Expression<BigDecimal> totalPaidExpr = cb.selectCase()
                .when(cb.equal(patientBillItem.get("entryType"), BillEntryType.Credit), patientBillItem.get("amount").as(BigDecimal.class))
                .otherwise(cb.literal(BigDecimal.ZERO)).as(BigDecimal.class);

        Expression<BigDecimal> balanceExpr = cb.diff(totalBillExpr, totalPaidExpr);


        cq.multiselect(
                patientBillItem.get("patientBill").get("visit").get("visitNumber"),
                patientBillItem.get("patientBill").get("patient").get("patientNumber"),
                patientBillItem.get("patientBill").get("patient").get("fullName"),
                patientBillItem.get("patientBill").get("visit").get("visitNumber"),
                patientBillItem.get("patientBill").get("visit").get("startDatetime").as(LocalDateTime.class),
                patientBillItem.get("patientBill").get("visit").get("visitType"),
                cb.sum(totalBillExpr).as(BigDecimal.class),
                cb.sum(totalPaidExpr).as(BigDecimal.class),
                cb.sum(balanceExpr).as(BigDecimal.class),
                patientBillItem.get("patientBill").get("visit").get("paymentMethod")
        );
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(patientBillItem.get("patientBill").get("walkinFlag"), false));

        if (visitNumber != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("visitNumber"), visitNumber));
        }
        if (paymentMethod != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("paymentMethod"), paymentMethod));
        }
        if (patientNumber != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("patient").get("patientNumber"), patientNumber));
        }

        if (range != null) {
            predicates.add(
                    cb.between(patientBillItem.get("patientBill").get("visit").get("startDatetime"), range.getStartDateTime(), range.getEndDateTime())
            );
        }
        if (search != null) {
            predicates.add(
                    cb.or(
                            cb.like(patientBillItem.get("patientBill").get("visit").get("visitNumber"), contains(search)),
                            cb.like(patientBillItem.get("patientBill").get("patient").get("patientNumber"), contains(search)),
                            cb.like(patientBillItem.get("patientBill").get("patient").get("fullName"), contains(search))
                    )
            );
        }

        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(patientBillItem.get("patientBill").get("visit").get("visitNumber"));

        List<PatientBillDetail> result = em.createQuery(cq).getResultList();

        return result;
    }
}

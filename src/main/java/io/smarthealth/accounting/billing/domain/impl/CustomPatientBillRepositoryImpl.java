package io.smarthealth.accounting.billing.domain.impl;

import io.smarthealth.accounting.billing.data.PatientBillDetail;
import io.smarthealth.accounting.billing.domain.CustomPatientBillRepository;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
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
    public List<PatientBillDetail> getPatientBills(String search, String patientNumber, String visitNumber, PaymentMethod paymentMethod, Long payerId, Long schemeId, VisitEnum.VisitType visitType, DateRange range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PatientBillDetail> cq = cb.createQuery(PatientBillDetail.class);
        Root<PatientBillItem> patientBillItem = cq.from(PatientBillItem.class);

        Predicate isDebit = cb.equal(patientBillItem.get("entryType"),BillEntryType.Debit);
        Predicate notCopay = cb.notEqual(patientBillItem.get("item").get("category"), ItemCategory.CoPay);
        Predicate isDebitAndNotCopay = cb.and(isDebit,notCopay);

        Expression<BigDecimal> totalBillExpr = cb.selectCase()
                .when(isDebitAndNotCopay, patientBillItem.get("amount").as(BigDecimal.class))
                .otherwise(cb.literal(BigDecimal.ZERO)).as(BigDecimal.class);

        Expression<BigDecimal> totalPaidExpr = cb.selectCase()
                .when(cb.equal(patientBillItem.get("entryType"), BillEntryType.Credit), patientBillItem.get("amount").as(BigDecimal.class))
                .otherwise(cb.literal(BigDecimal.ZERO)).as(BigDecimal.class);

        Expression<BigDecimal> balanceExpr = cb.diff(totalBillExpr, totalPaidExpr);

        //create a sub-query here
        Subquery<String> subquery = cq.subquery(String.class);
        Root<PaymentDetails> paymentDetail = subquery.from(PaymentDetails.class);

        Predicate visitPredicate = cb.equal(paymentDetail.get("visit").get("id"), patientBillItem.get("patientBill").get("visit").get("id"));

        Predicate payerPredicate = cb.equal( paymentDetail.get("payer").get("id"),payerId );
        Predicate schemePredicate = cb.equal( paymentDetail.get("scheme").get("id"), schemeId );

//        List<Predicate> subPredicates = new ArrayList<>();
//        if(payerId!=null) {
//            subPredicates.add(cb.equal(paymentDetail.get("payer").get("id"), payerId));
//        }
//        if(schemeId!=null){
//            subPredicates.add(cb.equal(paymentDetail.get("scheme").get("id"), schemeId));
//        }
//https://stackoverflow.com/questions/60059130/how-can-i-do-a-subquery-in-a-jpa-criteria-builder-select-statment
//        subquery.select(paymentDetail.get("visit"))
//                .distinct(true)
//                .where(subPredicates.toArray(new Predicate[0])); payer. scheme, ids
            subquery.select(paymentDetail.get("payer").get("payerName"))
                    .where(visitPredicate);

        Expression<String> payerSelection = cb.selectCase()
                .when(cb.equal(patientBillItem.get("patientBill").get("visit").get("paymentMethod"), PaymentMethod.Insurance), subquery.getSelection().as(String.class))
                .otherwise(cb.literal("-")).as(String.class);

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
                patientBillItem.get("patientBill").get("visit").get("paymentMethod"),
                subquery.getSelection()
        );
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(patientBillItem.get("patientBill").get("walkinFlag"), false));
        predicates.add(cb.notEqual(patientBillItem.get("status"), BillStatus.Canceled));


        if (visitNumber != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("visitNumber"), visitNumber));
        }
        if (paymentMethod != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("paymentMethod"), paymentMethod));
        }
        if (patientNumber != null) {
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("patient").get("patientNumber"), patientNumber));
        }
        if(visitType!=null){
            predicates.add(cb.equal(patientBillItem.get("patientBill").get("visit").get("visitType"), visitType));
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
//        if(payerId!=null){
//            predicates.add(cb.in(patientBillItem.get("patientBill").get("visit")).value(subquery));
//        }
//        if(schemeId!=null){
//            predicates.add(cb.in(patientBillItem.get("patientBill").get("visit")).value(subquery));
//        }

        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(patientBillItem.get("patientBill").get("visit").get("visitNumber"));

        List<PatientBillDetail> result = em.createQuery(cq).getResultList();

        return result;
    }
}

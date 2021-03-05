package io.smarthealth.accounting.billing.domain.impl;

import io.smarthealth.accounting.billing.data.PatientBillDetail;
import io.smarthealth.accounting.billing.data.VisitPaymentDto;
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

    //https://discourse.hibernate.org/t/how-can-i-do-a-join-fetch-in-criteria-api/846/3
    @Override
    public List<PatientBillDetail> getPatientBills(String search, String patientNumber, String visitNumber, PaymentMethod paymentMethod, Long payerId, Long schemeId, VisitEnum.VisitType visitType, DateRange range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PatientBillDetail> cq = cb.createQuery(PatientBillDetail.class);
        Root<PatientBillItem> patientBillItem = cq.from(PatientBillItem.class);

        Predicate isDebit = cb.equal(patientBillItem.get("entryType"), BillEntryType.Debit);
        Predicate notCopay = cb.notEqual(patientBillItem.get("item").get("category"), ItemCategory.CoPay);
        Predicate isDebitAndNotCopay = cb.and(isDebit, notCopay);

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
//        Join<Ereturn, ProductItem> paymentDetailsJoin = er.join("productItems", JoinType.LEFT);


        Predicate visitPredicate = cb.equal(paymentDetail.get("visit").get("id"), patientBillItem.get("patientBill").get("visit").get("id"));

        Predicate payerPredicate = cb.equal(paymentDetail.get("payer").get("id"), payerId);
        Predicate schemePredicate = cb.equal(paymentDetail.get("scheme").get("id"), schemeId);

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

        //CompoundSelection vdto = cb.construct(VisitPaymentDto.class, paymentDetail.get("payer").get("id").as(Long.class), paymentDetail.get("payer").get("payerName"), paymentDetail.get("scheme").get("id"), paymentDetail.get("scheme").get("schemeName"), paymentDetail.get("coPayValue"), paymentDetail.get("coPayCalcMethod"));

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
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("payer").get("id"),
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("payer").get("payerName"),
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("scheme").get("id"),
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("scheme").get("schemeName"),
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("coPayValue"),
                patientBillItem.get("patientBill").get("visit").get("paymentDetails").get("coPayCalcMethod").as(String.class)
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
        if (visitType != null) {
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

    //TODO re-write the above to this native
    /*

     SELECT v.start_datetime, v.stop_datetime, v.visit_number, v.visit_type,v.payment_method, v.STATUS AS visit_status, p.patient_number,concat(COALESCE(pt.given_name, ''), ' ', COALESCE(pt.middle_name, ''), ' ', COALESCE(pt.surname, ''))  as patient_name, pd.payer_id,pr.payer_name, pd.scheme_id,ps.scheme_name, pd.member_name, pd.policy_no,
     sum(case when entry_type='Debit'  then cast(pbi.amount as decimal(19,2)) else 0 end) as debit_amount,
     sum(case when entry_type='Credit' then cast(pbi.amount as decimal(19,2)) else 0 end) as credit_amount,
     SUM((case when pbi.entry_type='Debit' then cast(pbi.amount as decimal(19,2)) else 0 END)-(case when pbi.entry_type='Credit' then cast(pbi.amount as decimal(19,2)) else 0 end)) as balance
     FROM patient_visit v JOIN patient p ON v.patient_id = p.id INNER join person pt on pt.id=p.id LEFT JOIN patient_visit_payment_details pd ON v.id=pd.visit_id LEFT JOIN payers pr ON pd.payer_id = pr.id LEFT JOIN payer_scheme ps ON pd.scheme_id = ps.id
     JOIN patient_billing pb ON pb.visit_id = v.id JOIN patient_billing_item pbi ON pb.id = pbi.patient_bill_id GROUP BY v.visit_number
     ORDER BY 1 desc
    * */

    /*

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Ereturn> criteria = builder.createQuery( Ereturn.class );
        Root<Ereturn> er = criteria.from(Ereturn.class);
        Fetch<Ereturn, ProductItem> productItemFetch = er.fetch("productItems", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal( productItemFetch.get( "status" ), "RECEIVED"));
        criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        List<Ereturn> ers = em.createQuery( criteria ).getResultList();
    * */
}

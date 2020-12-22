/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface BillRepository {
    
    List<SummaryBill> getBillSummary(String visitNumber, String patientNumber, Boolean hasBalance, Boolean isWalkin,PaymentMethod paymentMode, DateRange range, Boolean includeCanceled,VisitEnum.VisitType visitType);
    
    BigDecimal getBillTotal(String visitNumber, Boolean includeCanceled, VisitEnum.VisitType visitType);
    
}

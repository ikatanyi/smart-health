/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.infrastructure.lang.DateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author Kelsas
 */
public interface BillSummaryRepository {

    Page<SummaryBill> getBillSummary(String visitNumber, String patientNumber, Boolean hasBalance, DateRange range, Pageable pageable);

    Page<SummaryBill> getWalkinBillSummary(String patientNumber, Boolean hasBalance, Pageable pageable);

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.data;

import java.time.LocalDate;
import java.util.List;
import io.smarthealth.accounting.payment.data.BilledItem;

/**
 *
 * @author Kelsas
 */
@lombok.Data
public class BillFinalizeData {

    private LocalDate billingDate;
    private String visitNumber;
    private String patientNumber;
    private List<BilledItem> billItems = new java.util.ArrayList<>();
}

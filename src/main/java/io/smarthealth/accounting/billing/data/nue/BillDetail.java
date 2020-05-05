/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.data.nue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BillDetail {

    private List<BillItem> bills = new ArrayList<>();
    private List<BillItem> paidBills = new ArrayList<>();
    private List<BillPayment> payments = new ArrayList<>();
    private BigDecimal totals;

}

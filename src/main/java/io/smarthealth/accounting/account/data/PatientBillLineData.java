/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.account.domain.PatientBillLine;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientBillLineData {

    private Long itemId;
    private String item;
    private String itemCode;
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private String transactionNo;
    private Long userId;
    private Long departrmentId;
    private String departmentName;
    private String userName;

    public static PatientBillLineData map(PatientBillLine bill) {
        PatientBillLineData data = new PatientBillLineData();
        data.setBillingDate(bill.getBillingDate());
        data.setPrice(bill.getPrice());
        data.setQuantity(bill.getQuantity());
        data.setItemId(bill.getItem().getId());
        if (bill.getItem() != null) {

            data.setItemCode(bill.getItem().getItemCode());
            data.setItem(bill.getItem().getItemName());
        }
        if (bill.getDepartrment() != null) {
            data.setDepartmentName(bill.getDepartrment().getName());
            data.setDepartrmentId(bill.getDepartrment().getId());
        }

        if (bill.getUser() != null) {

            data.setUserId(bill.getUser().getId());
            data.setUserName(bill.getUser().getUsername());
        }
        return data;
    }

    public static PatientBillLine map(PatientBillLineData bill) {
        PatientBillLine data = new PatientBillLine();
        data.setBillingDate(bill.getBillingDate());
        data.setPrice(bill.getPrice());
        data.setQuantity(bill.getQuantity());
//        data.setDepartmentName(bill.getDepartmentName());
//        data.setDepartrmentId(bill.getDepartrmentId());
//        data.setUserId(bill.getUserId());
//        data.setUserName(bill.getUserName());
//        data.setItemId(bill.getItemId());
//        data.setItemCode(bill.getItemCode());
//        data.setItem(bill.getItem());
        return data;
    }
}

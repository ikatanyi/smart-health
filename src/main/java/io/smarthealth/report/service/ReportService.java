/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.acc.data.v1.AccountEntry;
import io.smarthealth.accounting.acc.data.v1.AccountEntryPage;
import io.smarthealth.accounting.acc.data.v1.financial.statement.TrialBalance;
import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.accounting.acc.service.TrialBalancesServices;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.service.PaymentService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.accounts.DailyBillingData;
import io.smarthealth.report.data.accounts.InsuranceInvoiceData;
import io.smarthealth.report.data.accounts.InvoiceData;
import io.smarthealth.report.data.accounts.InvoiceItemData;
import io.smarthealth.report.data.accounts.TrialBalanceData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final JasperReportsService reportService;
    private final TrialBalancesServices trialBalanceService;
    private final BillingService billService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final AccountService accountService;
    private final VisitService visitService;
    

    public void getTrialBalance(ReportData reportData, final boolean includeEmptyEntries, HttpServletResponse response) throws SQLException {
        List<TrialBalanceData> dataList = new ArrayList();

        TrialBalance trialBalance = trialBalanceService.getTrialBalance(includeEmptyEntries);

        trialBalance.getTrialBalanceEntries().stream().map((trialBalEntry) -> {
            TrialBalanceData data = new TrialBalanceData();
            data.setCreditTotal(trialBalance.getCreditTotal());
            data.setDebitTotal(trialBalance.getDebitTotal());
            data.setCreatedBy(trialBalEntry.getLedger().getType());
            data.setCreatedOn(trialBalEntry.getLedger().getCreatedOn());
            data.setDescription(trialBalEntry.getLedger().getDescription());
            data.setLastModifiedBy(trialBalEntry.getLedger().getLastModifiedBy());
            data.setLastModifiedOn(trialBalEntry.getLedger().getLastModifiedOn());
            data.setName(trialBalEntry.getLedger().getIdentifier() + " - " + trialBalEntry.getLedger().getName());
            data.setParentLedgerIdentifier(trialBalEntry.getLedger().getParentLedgerIdentifier());
            data.setTotalValue(trialBalEntry.getLedger().getTotalValue());
            data.setType(trialBalEntry.getType());
            return data;
        }).forEachOrdered((data) -> {
            dataList.add(data);
        });
        reportData.setData(dataList);
        reportData.setReportName("/accounts/TrialBalance");
        reportService.generateReport(reportData, response);
    }

    public void getDailyPayment(ReportData reportData, HttpServletResponse response) throws SQLException {
        List<DailyBillingData> billData = new ArrayList();
        DateRange range = null;
        Map<String, Object> map = reportData.getFilters();
        String transactionNo = map.get("transactionNo") != null ? map.get("transactionNo").toString() : null;
        String visitNo = map.get("visitNo") != null ? map.get("visitNo").toString() : null;
        String patientNo = map.get("patientNo") != null ? map.get("patientNo").toString() : null;
        String paymentMode = map.get("paymentMode") != null ? map.get("paymentMode").toString() : null;
        String billNo = map.get("billNo") != null ? map.get("billNo").toString() : null;
        String dateRange = map.get("dateRange") != null ? map.get("dateRange").toString() : null;
        BillStatus billStatus = statusToEnum(String.valueOf(map.get("billStatus")));
        if (dateRange != null) {
            range = DateRange.fromIsoStringOrReturnNull(dateRange);
        }
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<PatientBill> bills = billService.findAllBills(transactionNo, visitNo, patientNo, paymentMode, billNo, billStatus, range, pageable).getContent();
        for (PatientBill bill : bills) {
            DailyBillingData data = new DailyBillingData();
            data.setAmount(bill.getAmount());
            data.setBalance(bill.getBalance());
            data.setCreatedBy(bill.getCreatedBy());
            data.setCreatedOn(bill.getBillingDate());
            data.setPatientId(bill.getPatient().getPatientNumber());
            data.setPatientName(bill.getPatient().getFullName());
            data.setPaymentMode(bill.getPaymentMode());
            data.setPaid(bill.getAmount() - bill.getBalance());
            for (PatientBillItem item : bill.getBillItems()) {
                switch (item.getServicePoint()) {
                    case "Laboratory":
                        data.setLab(+item.getAmount());
                        break;
                    case "Pharmacy":
                        data.setPharmacy(+item.getAmount());
                        break;
                    case "Procedure":
                        data.setProcedure(+item.getAmount());
                        break;
                    case "Radiology":
                        data.setRadiology(+item.getAmount());
                        break;
                    case "Consultation":
                        data.setAmount(+item.getAmount());
                        break;
                    default:
                        data.setOther(+item.getAmount());
                        break;
                }

            }
            billData.add(data);
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("PatientId");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("SUBREPORT_DIR", "/accounts/");
        reportData.setData(billData);
        reportData.setReportName("/accounts/payment_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getInvoiceStatement(ReportData reportData, HttpServletResponse response) throws SQLException {
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        DateRange range = null;
        Map<String, Object> map = reportData.getFilters();
        Long payer = map.get("transactionNo") != null ? Long.parseLong(map.get("payer").toString()) : null;
        Long Scheme = map.get("scheme") != null ? Long.parseLong(map.get("scheme").toString()): null;
        String patientNo = map.get("patientNo") != null ? map.get("patientNo").toString() : null;
        String invoiceNo = map.get("invoiceNo") != null ? map.get("paymentMode").toString() : null;
        String dateRange = map.get("dateRange") != null ? map.get("dateRange").toString() : null;
        InvoiceStatus status = invoiceStatusToEnum(String.valueOf(map.get("billStatus")));
        if (dateRange != null) {
            range = DateRange.fromIsoStringOrReturnNull(dateRange);
        }
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, Scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
        for (Invoice invoice : invoices) {
            InsuranceInvoiceData data = new InsuranceInvoiceData();
            data.setAmount(invoice.getTotal());
            data.setBalance(invoice.getBalance());
            data.setDiscount(invoice.getDisounts());
            data.setPatientId(invoice.getBill().getPatient().getPatientNumber());
            data.setPatientName(invoice.getBill().getPatient().getFullName());
            data.setDueDate(String.valueOf(invoice.getDueDate()));
            data.setInvoiceNo(invoice.getNumber());
            data.setPayer(invoice.getPayer().getPayerName());
            data.setPayee(invoice.getPayee().getSchemeName());
            data.setStatus(invoice.getStatus().name());
            data.setDate(String.valueOf(invoice.getDate()));
            data.setPaid(invoice.getTotal() - invoice.getBalance());
            for (InvoiceLineItem item : invoice.getItems()) {
                switch (item.getBillItem().getServicePoint()) {
                    case "Laboratory":
                        data.setLab(+item.getBillItem().getAmount());
                        break;
                    case "Pharmacy":
                        data.setPharmacy(+item.getBillItem().getAmount());
                        break;
                    case "Procedure":
                        data.setProcedure(+item.getBillItem().getAmount());
                        break;
                    case "Radiology":
                        data.setRadiology(+item.getBillItem().getAmount());
                        break;
                    case "Consultation":
                        data.setAmount(+item.getBillItem().getAmount());
                        break;
                    default:
                        data.setOther(+item.getBillItem().getAmount());
                        break;
                }
            }
            invoiceData.add(data);
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("date");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("SUBREPORT_DIR", "/accounts/");
        reportData.setData(invoiceData);
        reportData.setReportName("/accounts/insurance_invoice_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void genInsuranceStatement(ReportData reportData, HttpServletResponse response) throws SQLException {
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        DateRange range = null;
        Map<String, Object> map = reportData.getFilters();
        Long payer = map.get("payer") != null ? Long.parseLong(map.get("payer").toString()) : null;
        Long Scheme = map.get("scheme") != null ? Long.parseLong(map.get("scheme").toString()): null;
        String patientNo = map.get("patientNo") != null ? map.get("patientNo").toString() : null;
        String invoiceNo = map.get("invoiceNo") != null ? map.get("paymentMode").toString() : null;
        String dateRange = map.get("dateRange") != null ? map.get("dateRange").toString() : null;
        InvoiceStatus status = invoiceStatusToEnum(String.valueOf(map.get("billStatus")));
        if (dateRange != null) {
            range = DateRange.fromIsoStringOrReturnNull(dateRange);
        }
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, Scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
        for (Invoice invoice : invoices) {
            InsuranceInvoiceData data = new InsuranceInvoiceData();
            data.setAmount(invoice.getTotal());
            data.setBalance(invoice.getBalance());
            data.setDiscount(invoice.getDisounts());
            data.setPatientId(invoice.getBill().getPatient().getPatientNumber());
            data.setPatientName(invoice.getBill().getPatient().getFullName());
            data.setDueDate(String.valueOf(invoice.getDueDate()));
            data.setInvoiceNo(invoice.getNumber());
            data.setPayer(invoice.getPayer().getPayerName());
            data.setPayee(invoice.getPayee().getSchemeName());
            data.setStatus(invoice.getStatus().name());
            data.setDate(String.valueOf(invoice.getDate()));
            invoiceData.add(data);
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("date");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("SUBREPORT_DIR", "/accounts/");
        reportData.setData(invoiceData);
        reportData.setReportName("/accounts/insurance_statement");
        reportService.generateReport(reportData, response);
    }
    
     public void getInvoice(ReportData reportData, HttpServletResponse response) throws SQLException {
        List<InvoiceData> invoiceData = new ArrayList();
        DateRange range = null;
        Map<String, Object> map = reportData.getFilters();
        Long payer = map.get("transactionNo") != null ? Long.parseLong(map.get("payer").toString()) : null;
        Long Scheme = map.get("scheme") != null ? Long.parseLong(map.get("scheme").toString()): null;
        String patientNo = map.get("patientNo") != null ? map.get("patientNo").toString() : null;
        String invoiceNo = map.get("invoiceNo") != null ? map.get("paymentMode").toString() : null;
        String dateRange = map.get("dateRange") != null ? map.get("dateRange").toString() : null;
        InvoiceStatus status = invoiceStatusToEnum(String.valueOf(map.get("billStatus")));
        if (dateRange != null) {
            range = DateRange.fromIsoStringOrReturnNull(dateRange);
        }
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, Scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
        for (Invoice invoice : invoices) {
            List<InvoiceItemData>itemArray = new ArrayList();
            InvoiceData data = new InvoiceData();
            data.setAmount(invoice.getTotal());
            data.setBalance(invoice.getBalance());
            data.setDiscount(invoice.getDisounts());
            data.setPatientId(invoice.getBill().getPatient().getPatientNumber());
            data.setPatientName(invoice.getBill().getPatient().getFullName());
            data.setDueDate(String.valueOf(invoice.getDueDate()));
            data.setInvoiceNo(invoice.getNumber());
            data.setPayer(invoice.getPayer().getPayerName());
            data.setPayee(invoice.getPayee().getSchemeName());
            data.setDate(String.valueOf(invoice.getDate()));
            data.setCreatedBy(invoice.getCreatedBy());
            for(InvoiceLineItem invoiceLineItem:invoice.getItems()){
                InvoiceItemData item = new InvoiceItemData();
                item.setQuantity(invoiceLineItem.getBillItem().getQuantity());
                item.setAmount(invoiceLineItem.getBillItem().getAmount());
                item.setBalance(invoiceLineItem.getBillItem().getBalance());
                item.setDiscount(invoiceLineItem.getBillItem().getDiscount());
                item.setItem(invoiceLineItem.getBillItem().getItem()!=null?invoiceLineItem.getBillItem().getItem().getItemName():"");
                item.setPrice(invoiceLineItem.getBillItem().getPrice());
                item.setServicePoint(invoiceLineItem.getBillItem().getServicePoint());
                item.setTaxes(invoiceLineItem.getBillItem().getTaxes());
                item.setBillingDate(String.valueOf(invoiceLineItem.getBillItem().getBillingDate()));
                itemArray.add(item);
            }
            data.setItems(itemArray);            
            invoiceData.add(data);
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("date");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("SUBREPORT_DIR", "/accounts/");
        reportData.setData(invoiceData);
        reportData.setReportName("/accounts/invoice");
        reportService.generateReport(reportData, response);
    }
     
     public void getAccountEntries(final String identifier,
            final DateRange range,
            final String message,
            final Pageable pageable){
         List<AccountEntry> accountEntries = accountService.fetchAccountEntries(identifier, range, message, pageable).getAccountEntries();
         
     }
     
     public void getPatientFile(final String PatientId){
         
     }

    private BillStatus statusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(BillStatus.class, status)) {
            return BillStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
    
    private InvoiceStatus invoiceStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(InvoiceStatus.class, status)) {
            return InvoiceStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Invoice Status");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.accounts.service.TrialBalanceService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.accounting.payment.service.ReceivePaymentService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.accounts.DailyBillingData;
import io.smarthealth.report.data.accounts.InsuranceInvoiceData;
import io.smarthealth.report.data.accounts.TrialBalanceData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class AccountReportService {

    private final JasperReportsService reportService;
    private final TrialBalanceService trialBalanceService;
    private final BillingService billService;
    private final InvoiceService invoiceService;
    private final VisitService visitService;
    private final PatientService patientService;
    private final ReceivePaymentService paymentService;
    
    
    private final PrescriptionService prescriptionService;
    
   

    public void getTrialBalance(MultiValueMap<String,String>reportParam,  ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
     
        Boolean includeEmptyEntries = Boolean.valueOf(reportParam.getFirst("includeEmptyEntries"));
        ReportData reportData = new ReportData();
        List<TrialBalanceData> dataList  = trialBalanceService.getTrialBalance(includeEmptyEntries).getTrialBalanceEntries()
           .stream()
           .map((trialBalEntry) -> {
            TrialBalanceData data = new TrialBalanceData();
//            data.setCreditTotal(trialBalEntry.getCreditTotal());
//            data.setDebitTotal(trialBalance.getDebitTotal());
//            data.setCreatedBy(trialBalEntry.getLedger().getType());
            data.setCreatedOn(trialBalEntry.getLedger().getCreatedOn());
            data.setDescription(trialBalEntry.getLedger().getDescription());
//            data.setLastModifiedBy(trialBalEntry.getLedger().getLastModifiedBy());
//            data.setLastModifiedOn(trialBalEntry.getLedger().getLastModifiedOn());
            data.setName(trialBalEntry.getLedger().getIdentifier() + " - " + trialBalEntry.getLedger().getName());
            data.setParentLedgerIdentifier(trialBalEntry.getLedger().getParentLedgerIdentifier());
            data.setTotalValue(trialBalEntry.getLedger().getTotalValue());
            data.setType(trialBalEntry.getType());
            return data;
        }).collect(Collectors.toList());
        reportData.setData(dataList);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/TrialBalance");
        reportData.setReportName("trialBalance");
        reportService.generateReport(reportData, response);
    }

    public void getDailyPayment(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        
        String transactionNo = reportParam.getFirst("transactionNo");
        String paymentMode = reportParam.getFirst("paymentMode");
        String billNo = reportParam.getFirst("billNo");
        String visitNo = reportParam.getFirst("visitNo");
        String billStatus = reportParam.getFirst("billStatus");
        String patientNo = reportParam.getFirst("patientNo");
        String dateRange = reportParam.getFirst("dateRange"); 
        
        List<DailyBillingData> billData = new ArrayList();
        ReportData reportData = new ReportData();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<PatientBill> bills = billService.findAllBills(transactionNo, visitNo, patientNo, paymentMode, billNo, statusToEnum(billStatus), range, pageable).getContent();
        for (PatientBill bill : bills) {
            DailyBillingData data = new DailyBillingData();
            data.setAmount(bill.getAmount());
            data.setBalance(bill.getBalance());
            data.setCreatedBy(bill.getCreatedBy());
            data.setCreatedOn(bill.getBillingDate());
            if(bill.getPatient()!=null){
                data.setPatientId(bill.getPatient().getPatientNumber());
                data.setPatientName(bill.getPatient().getFullName());
            }
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
        sortField.setName("paymentMode");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField.setName("patientId");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);

        reportData.setData(billData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/payment_statement");
        reportData.setReportName("Payement_Statement");
        reportService.generateReport(reportData, response);
    }

    public void getInvoiceStatement(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        String transactionNo = reportParam.getFirst("transactionNo");
        Long payer  = Long.getLong(reportParam.getFirst("payerId"),null);
        Long scheme = Long.getLong(reportParam.getFirst("schemeId"),null);
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo"); 
        String dateRange = reportParam.getFirst("dateRange"); 
        String invoiceStatus = reportParam.getFirst("invoiceStatus");
        Double amountGreaterThan = 0.0;
        Boolean filterPastDue = false;
        Double amountLessThanOrEqualTo = 0.0;
        
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        ReportData reportData = new ReportData();
        InvoiceStatus status = invoiceStatusToEnum(invoiceStatus);

        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<InvoiceData> invoices = invoiceService.fetchInvoices(payer, scheme, invoiceNo, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo, pageable).getContent()
                .stream()
                .map((invoice)->invoice.toData())
                .collect(Collectors.toList());

        for (InvoiceData invoice : invoices) {
            InsuranceInvoiceData data = new InsuranceInvoiceData();
            data.setAmount(invoice.getAmount());
            data.setBalance(invoice.getBalance());
            data.setDiscount(invoice.getDiscount());
            data.setMemberName(invoice.getMemberName());
            data.setMemberNumber(invoice.getMemberNumber());
            data.setPatientId(invoice.getPatientNumber());
            data.setPatientName(invoice.getPatientName());
            data.setDueDate(invoice.getDueDate());
            data.setNumber(invoice.getNumber());
            data.setPayer(invoice.getPayer());
            data.setScheme(invoice.getScheme());
            data.setStatus(invoice.getStatus());
            data.setDate(invoice.getInvoiceDate());
            data.setPaid(invoice.getAmount().subtract(invoice.getBalance()));
            for (InvoiceItemData item : invoice.getInvoiceItems()) {
                switch (item.getServicePoint()) {
                    case "Laboratory":
                        data.setLab(data.getLab().add(item.getAmount()));
                        break;
                    case "Pharmacy":
                        data.setPharmacy(data.getPharmacy().add(item.getAmount()));
                        break;
                    case "Procedure":
                        data.setProcedure(data.getProcedure().add(item.getAmount()));
                        break;
                    case "Radiology":
                        data.setRadiology(data.getRadiology().add(item.getAmount()));
                        break;
                    case "Consultation":
                        data.setConsultation(data.getConsultation().add(item.getAmount()));
                        break;
                    default:
                        data.setOther(data.getOther()!=null?data.getOther().add(item.getAmount()):item.getAmount());
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
        sortField.setName("payer");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_invoice_statement");
        reportData.setReportName("invoice_statement");
        reportService.generateReport(reportData, response);
    }

    public void genInsuranceStatement(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        Long payer  = Long.getLong(reportParam.getFirst("payerId"),null);
        Long scheme = Long.getLong(reportParam.getFirst("schemeId"),null);
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo"); 
        String dateRange = reportParam.getFirst("dateRange"); 
        InvoiceStatus status = invoiceStatusToEnum(reportParam.getFirst("invoiceStatus"));       
        
        Double amountGreaterThan = 0.0;
        Boolean filterPastDue = false;
        Double amountLessThanOrEqualTo = 0.0;
        
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<InvoiceData> invoiceData = invoiceService.fetchInvoices(payer, scheme, invoiceNo, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo, pageable).getContent()
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitDate");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField = new JRDesignSortField();
        sortField.setName("payer");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_statement");
        reportData.setReportName("insurance_statement");
        reportService.generateReport(reportData, response);
    }

    public void getInvoice(MultiValueMap<String,String>reportParam,  ExportFormat format,  HttpServletResponse response) throws SQLException, JRException, IOException {
        
        String invoiceNo = reportParam.getFirst("invoiceNo"); 
        ReportData reportData = new ReportData();
       
        InvoiceData invoiceData = (invoiceService.getInvoiceByNumberOrThrow(invoiceNo)).toData();                
        
//        List<JRSortField> sortList = new ArrayList<>();
//        JRDesignSortField sortField = new JRDesignSortField();
//        sortField.setName("date");
//        sortField.setOrder(SortOrderEnum.ASCENDING);
//        sortField.setType(SortFieldTypeEnum.FIELD);
//        sortList.add(sortField);
//        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(Arrays.asList(invoiceData));
        reportData.setTemplate("/accounts/invoice");
        reportData.setReportName("invoice");
        reportData.setFormat(format);
        reportService.generateReport(reportData, response);
    }
    
    public void getPatientReceipt(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String receiptNo= reportParam.getFirst("receiptNo"); 
        //"RCT-00009"
        ReceiptData receiptData = paymentService.getPaymentByReceiptNumber(receiptNo).toData();
        reportData.setData(Arrays.asList(receiptData));
        reportData.setFormat(format);
        reportData.setTemplate("/payments/general_receipt");
        reportData.setReportName("Receipt"+receiptNo);
        reportService.generateReport(reportData, response);
    }    
    
//     
//     public void getAccountEntries(final String identifier,
//            final DateRange range,
//            final String message,
//            final Pageable pageable){
//         List<Account> accountEntries = accountService.fetchAccountEntries(identifier, range, message, pageable).getAccountEntries();
//         
//     }
//     

   

    

    

    

    

    

    

    

    private BillStatus statusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(BillStatus.class, status)) {
            return BillStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }

    private ExportFormat ExportFormatToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(ExportFormat.class, status)) {
            return ExportFormat.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Export Status");
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

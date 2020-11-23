/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.accounts.service.LedgerService;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.data.ShiftPayment;
import io.smarthealth.accounting.cashier.service.CashierService;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.data.PettyCashPaymentData;
import io.smarthealth.accounting.payment.data.SupplierPaymentData;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.accounting.payment.service.PaymentService;
import io.smarthealth.accounting.payment.service.ReceivePaymentService;
import io.smarthealth.accounting.pettycash.data.PettyCashRequestsData;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.service.PettyCashRequestsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.claim.creditNote.service.CreditNoteService;
import io.smarthealth.debtor.payer.data.PayerData;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.EnglishNumberToWords;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class PaymentReportService {

    private final JasperReportsService reportService;
    private final SupplierService supplierService;
    private final PaymentService paymentService;
    private final LedgerService ledgerService;
    private final CreditNoteService creditNoteService;

    private final VisitService visitService;
    private final InvoiceService invoiceService;
    private final EmployeeService employeeService;
    private final PettyCashRequestsService pettyCashRequestService;
    private final PayerService payerService;
    private final ReceivePaymentService receivePaymentService;
    private final CashierService cashierService;

    public void getPettyCashRequests(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        String requestNo = reportParam.getFirst("requestNo");
        String staffNumber = reportParam.getFirst("staffNumber");
        String dateRange = reportParam.getFirst("range");
        PettyCashStatus status = PettyCashStatusToEnum(reportParam.getFirst("status"));
        Employee employee = null;
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(staffNumber);
        if (emp.isPresent()) {
            employee = emp.get();
        }

        List<PettyCashRequestsData> pettyCashData = pettyCashRequestService.findPettyCashRequests(requestNo, employee, status, Pageable.unpaged())
                .getContent()
                .stream()
                .map(x -> PettyCashRequestsData.map(x))
                .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("status");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);

        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        reportData.setData(pettyCashData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/pettyCash_statement");
        reportData.setReportName("pettycash_requests_statement");
        reportService.generateReport(reportData, response);
    }

    public void getPettyCash(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        String requestNo = reportParam.getFirst("requestNo");
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        PettyCashRequestsData pettyCashData = PettyCashRequestsData.map(pettyCashRequestService.fetchCashRequestByRequestNo(requestNo));

        reportData.setData(Arrays.asList(pettyCashData));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/petty_cash");
        reportData.setReportName("pettycash_request_form");
        reportService.generateReport(reportData, response);
    }

    public void getInvoice(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {

        String invoiceNo = reportParam.getFirst("invoiceNo");
        ReportData reportData = new ReportData();

        InvoiceData invoiceData = (invoiceService.getInvoiceByNumberOrThrow(invoiceNo)).toData();
        reportData.setData(Arrays.asList(invoiceData));
        reportData.setTemplate("/accounts/invoice");
        reportData.setReportName("invoice");
        reportData.setFormat(format);
        reportService.generateReport(reportData, response);
    }

    public void getPaymentVoucher(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String voucherNo = reportParam.getFirst("voucherNo");

        PaymentData paymentData = paymentService.getPaymentByVoucherNo(voucherNo).toData();

        reportData.getFilters().put("category", paymentData.getPayeeType().toString());
        if (paymentData.getPayeeType() == PayeeType.Doctor || paymentData.getPayeeType() == PayeeType.PettyCash) {
            if (paymentData.getPayeeId() != null) {
                Employee emp = employeeService.findEmployeeByIdOrThrow(paymentData.getPayeeId());
                reportData.setEmployeeId(emp.getStaffNumber());
            }
        }
        if (paymentData.getPayeeType() == PayeeType.Supplier) {
            Optional<Supplier> supplier = supplierService.getSupplierById(paymentData.getPayeeId());
            if (supplier.isPresent()) {
                reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));
            }
        }
        if (paymentData.getPayeeType() == PayeeType.Doctor || paymentData.getPayeeType() == PayeeType.Supplier) {
            List<SupplierPaymentData> paymentDataItem = paymentService.getSupplierPayment(paymentData.getId(), paymentData.getPayeeType());
            reportData.getFilters().put("PaymentData", paymentDataItem);
        }
        if (paymentData.getPayeeType() == PayeeType.PettyCash) {
            List<PettyCashPaymentData> paymentDataItem = paymentService.getPettyCashPayment(paymentData.getId());
            reportData.getFilters().put("PaymentData", paymentDataItem);
        }
        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(paymentData.getAmount()).toUpperCase());
        reportData.setData(Arrays.asList(paymentData));
        reportData.setFormat(format);
        reportData.setTemplate("/payments/payment_voucher");
        reportData.setReportName("Payment-voucher" + voucherNo);
        reportService.generateReport(reportData, response);
    }

    public void getcreditNote(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String creditNoteNo = reportParam.getFirst("creditNoteNo");

        CreditNoteData creditNoteData = creditNoteService.getCreditNoteByNumberWithFailDetection(creditNoteNo).toData();

        reportData.getFilters().put("category", "Payer");
        PayerData payerData = PayerData.map(payerService.findPayerByIdWithNotFoundDetection(creditNoteData.getPayerId()));
        reportData.getFilters().put("Payer_Data", Arrays.asList(payerData));

        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(BigDecimal.valueOf(creditNoteData.getAmount())).toUpperCase());
        reportData.setData(Arrays.asList(creditNoteData));
        reportData.setFormat(format);
        reportData.setTemplate("/payments/credit_note");
        reportData.setReportName("Credit-Note" + creditNoteNo);
        reportService.generateReport(reportData, response);
    }

    public void getPaymentStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        PayeeType creditorType = PayeeTypeToEnum(reportParam.getFirst("creditorType"));
        Long creditorId = NumberUtils.createLong(reportParam.getFirst("creditorId"));
        String creditor = reportParam.getFirst("creditor");
        String transactionNo = reportParam.getFirst("transactionNo");
        String dateRange = reportParam.getFirst("range");
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        List<PaymentData> patientData = paymentService.getPayments(creditorType, creditorId, creditor, transactionNo, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((register) -> register.toData())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        reportData.setTemplate("/payment/payment_statement");

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("transactionNo");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        reportData.setReportName("Payment-Statement");
        reportService.generateReport(reportData, response);
    }
    
    
    public void shiftPayments(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        Long cashierId = NumberUtils.createLong(reportParam.getFirst("cashierId"));
        String shiftNo = reportParam.getFirst("shiftNo");
        
        List<CashierShift> paymentshiftData = receivePaymentService.getCashierShift(shiftNo, cashierId);
        List<ShiftPayment> shiftPayment = cashierService.getShiftByMethod(shiftNo);
        
        reportData.getFilters().put("PaymentData", shiftPayment);
        reportData.getFilters().put("CashierShiftData", paymentshiftData);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        if(!paymentshiftData.isEmpty())
           reportData.getFilters().put("Cashier_Data", Arrays.asList(cashierService.getCashier(paymentshiftData.get(0).getCashierId()).toData()));
      
//        reportData.setData(paymentshiftData);
        reportData.setFormat(format);
        reportData.setTemplate("/payments/shift_report");
        reportData.setReportName("Shift-Report"+shiftNo);
        reportService.generateReport(reportData, response);
    }

    private PettyCashStatus PettyCashStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(PettyCashStatus.class, status)) {
            return PettyCashStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid PettyCash Status");
    }

    private PayeeType PayeeTypeToEnum(String creditorType) {
        if (creditorType == null || creditorType.equals("null") || creditorType.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(PayeeType.class, creditorType)) {
            return PayeeType.valueOf(creditorType);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
}

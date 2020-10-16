/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.accounts.data.AccountData;
import io.smarthealth.accounting.accounts.data.ChartOfAccountEntry;
import io.smarthealth.accounting.accounts.data.JournalEntryData;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import io.smarthealth.accounting.accounts.data.LedgerData;
import io.smarthealth.accounting.accounts.data.financial.statement.FinancialConditionSection;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatementSection;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.IncomeExpenseData;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.accounting.accounts.service.ChartOfAccountServices;
import io.smarthealth.accounting.accounts.service.FinancialConditionService;
import io.smarthealth.accounting.accounts.service.IncomesStatementService;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.accounts.service.LedgerService;
import io.smarthealth.accounting.accounts.service.TrialBalanceService;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.data.SummaryBill;
import io.smarthealth.accounting.billing.data.nue.BillDetail;
import io.smarthealth.accounting.billing.data.nue.BillItem;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.accounting.payment.data.ReceiptItemData;
import io.smarthealth.accounting.payment.data.ReceiptTransactionData;
import io.smarthealth.accounting.payment.data.RemittanceData;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.service.ReceivePaymentService;
import io.smarthealth.accounting.payment.service.RemittanceService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.PaymentMethod;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.debtor.claim.dispatch.data.DispatchData;
import io.smarthealth.debtor.claim.dispatch.service.DispatchService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.accounts.DailyBillingData;
import io.smarthealth.report.data.accounts.InsuranceInvoiceData;
import io.smarthealth.report.data.accounts.ReportReceiptData;
import io.smarthealth.report.data.accounts.TrialBalanceData;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
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
    private final AccountService accountService;
    private final ChartOfAccountServices chartOfAccountsService;
    private final PrescriptionService prescriptionService;
    private final FinancialConditionService financialConditionService;
    private final IncomesStatementService incomesStatementService;
    private final JournalService journalService;
    private final LedgerService ledgerService;
    private final RemittanceService remittanceService;
    private final AllocationService allocationService;
    private final ReceivePaymentService receivePaymentService;
    private final DispatchService dispatchService;
    
    public void getTrialBalance(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        
        Boolean includeEmptyEntries = Boolean.valueOf(reportParam.getFirst("includeEmptyEntries"));
        LocalDate asAt = DateConverter.dateFromIsoString(reportParam.getFirst("asAt"));
        ReportData reportData = new ReportData();
        List<TrialBalanceData> dataList = trialBalanceService.getTrialBalance(includeEmptyEntries, asAt).getTrialBalanceEntries()
                .stream()
                .map((trialBalEntry) -> {
                    TrialBalanceData data = new TrialBalanceData();
                    data.setCreatedOn(trialBalEntry.getLedger().getCreatedOn());
                    data.setDescription(trialBalEntry.getLedger().getDescription());
                    data.setName(trialBalEntry.getLedger().getIdentifier() + " - " + trialBalEntry.getLedger().getName());
                    data.setParentLedgerIdentifier(trialBalEntry.getLedger().getParentLedgerIdentifier());
                    data.setTotalValue(trialBalEntry.getLedger().getTotalValue());
                    data.setType(trialBalEntry.getType());
                    return data;
                }).collect(Collectors.toList());
        reportData.setData(dataList);
        reportData.getFilters().put("asAt", reportParam.getFirst("asAt"));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/TrialBalance");
        reportData.setReportName("trialBalance");
        reportService.generateReport(reportData, response);
    }
    
    public void getAccounts(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        Boolean includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;
        String term = reportParam.getFirst("term");
        AccountType type = AccountTypeToEnum(reportParam.getFirst("type"));
        Boolean includeCustomerAccounts = reportParam.getFirst("includeCustomerAccounts") != null ? Boolean.getBoolean(reportParam.getFirst("includeCustomerAccounts")) : null;
        ReportData reportData = new ReportData();
        List<AccountData> accountData = accountService.fetchAccounts(includeClosed, term, type, includeCustomerAccounts, Pageable.unpaged()).getAccounts();
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("type");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setData(accountData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/accounts");
        reportData.setReportName("accounts_Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getIncomeExpenseAccounts(ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        List incomeExpeData = new ArrayList();
        IncomeExpenseData accountData = accountService.getIncomeExpenseAccounts();
        
        incomeExpeData.addAll(accountData.getIncomeAccounts());
        incomeExpeData.addAll(accountData.getExpensesAccounts());
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("type");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setData(incomeExpeData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/income_expense_account");
        reportData.setReportName("income-expense-accounts");
        reportService.generateReport(reportData, response);
    }
    
    public void getChartOfAccounts(ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        List<ChartOfAccountEntry> chartofAccount = chartOfAccountsService.getChartOfAccounts();
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("type");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setData(chartofAccount);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/chartOfAccount");
        reportData.setReportName("chart-of-account");
        reportService.generateReport(reportData, response);
    }
    
    public void getBalanceSheet(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        LocalDate asAt = DateConverter.dateFromIsoString(reportParam.getFirst("asAt"));
        List<FinancialConditionSection> financialConditionSection = financialConditionService.getFinancialCondition(asAt).getFinancialConditionSections();
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("type");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setData(financialConditionSection);
        reportData.getFilters().put("asAt", reportParam.getFirst("asAt"));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/balance_sheet");
        reportData.setReportName("balance_sheet");
        reportService.generateReport(reportData, response);
    }
    
    public void getIncomeStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        LocalDate asAt = DateConverter.dateFromIsoString(reportParam.getFirst("asAt"));
        List<IncomeStatementSection> incomeStatement = incomesStatementService.getIncomeStatement(asAt).getIncomeStatementSections();
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("type");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("asAt", reportParam.getFirst("asAt"));
        reportData.setData(incomeStatement);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/income_statement");
        reportData.setReportName("income_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getRemittanceReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long payerId = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Boolean hasBalance = reportParam.getFirst("hasBalance") != null ? Boolean.getBoolean(reportParam.getFirst("hasBalance")) : null;
        String receipt = reportParam.getFirst("receipt");
        String remittanceNo = reportParam.getFirst("remittanceNo");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        
        List<RemittanceData> remittanceData = remittanceService.getRemittances(payerId, receipt, remittanceNo, hasBalance, range, Pageable.unpaged()).getContent()
                .stream()
                .map(rem -> (rem.toData()))
                .collect(Collectors.toList());
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("remittanceDate");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        sortField = new JRDesignSortField();
        sortField.setName("payer");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(remittanceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/remittance_report");
        reportData.setReportName("Remittance_Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getAllocationReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long payerId = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Long schemeId = NumberUtils.createLong(reportParam.getFirst("schemeId"));
        String receiptNo = reportParam.getFirst("receiptNo");
        String invoiceNo = reportParam.getFirst("invoiceNo");
        String remittanceNo = reportParam.getFirst("remittanceNo");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        
        List<AllocationData> remittanceData = allocationService.getAllocations(invoiceNo, receiptNo, remittanceNo, payerId, schemeId, range, Pageable.unpaged()).getContent()
                .stream()
                .map(rem -> (rem.map()))
                .collect(Collectors.toList());
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("payer");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        sortField = new JRDesignSortField();
        sortField.setName("transactionDate");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(remittanceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/allocation_trans_report");
        reportData.setReportName("Allocation-Report");
        reportService.generateReport(reportData, response);
    }
    
    public void getJournal(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long journalId = NumberUtils.createLong(reportParam.getFirst("journalId"));
        JournalEntryData journalData = journalService.findJournalIdOrThrow(journalId).toData();
        
        reportData.setData(Arrays.asList(journalData));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/journal_report");
        reportData.setReportName("journal-statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getAccTransactions(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String identifier = reportParam.getFirst("identifier");
        String dateRange = reportParam.getFirst("range");
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        AccountData data = accountService.findAccount(identifier)
                .orElseThrow(() -> APIException.notFound("Account with identifier {0} not found", identifier));
        reportData.getFilters().put("accountNo", data.getIdentifier());
        reportData.getFilters().put("accountName", data.getName());
        reportData.getFilters().put("accountType", data.getType());
        reportData.getFilters().put("status", data.getState());
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.getFilters().put("ledger", data.getLedger());
        reportData.getFilters().put("balance", data.getBalance());
        
        List<JournalEntryItemData> list = accountService.getAccountTransaction(identifier, range);
        
        reportData.setData(list);
        reportData.setFormat(format);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setTemplate("/accounts/account_transactions_report");
        reportData.setReportName("Account-Transactionst");
        reportService.generateReport(reportData, response);
    }
    
    public void getDailyPayment(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        
        String transactionNo = reportParam.getFirst("transactionNo");
        PaymentMethod paymentMode = PaymentMethodToEnum(reportParam.getFirst("paymentMode"));
        String visitNumber = reportParam.getFirst("visitNumber");
        String patientNumber = reportParam.getFirst("patientNo");
        Boolean hasBalance = reportParam.getFirst("hasbalance") != null ? Boolean.valueOf(reportParam.getFirst("hasbalance")) : null;
        Boolean isWalkin = reportParam.getFirst("isWakin") != null ? Boolean.valueOf(reportParam.getFirst("isWakin")) : null;
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        
        List<DailyBillingData> billData = new ArrayList();
        ReportData reportData = new ReportData();
        List<SummaryBill> bills = billService.getBillTotals(visitNumber, patientNumber, hasBalance, isWalkin, paymentMode, range);
        for (SummaryBill bill : bills) {
            DailyBillingData data = new DailyBillingData();
            
            data.setBalance(bill.getBalance());
            data.setCreatedOn(bill.getDate());
            data.setVisitNo(bill.getVisitNumber());
            data.setIsWalkin(bill.getIsWalkin());
            data.setPatientId(bill.getPatientNumber());
            data.setPatientName(bill.getPatientName());
            data.setPaymentMode(bill.getPaymentMethod());
            data.setBalance(bill.getBalance());
            List<BillItem> items = billService.getAllBillDetails(bill.getVisitNumber());
            for (BillItem item : items) {
                data.setAmount(data.getAmount().add(NumberUtils.toScaledBigDecimal(item.getAmount())));
                if (item.getStatus() == item.getStatus().Paid) {
                    data.setPaid(data.getPaid().add(NumberUtils.toScaledBigDecimal(item.getAmount())));
                }
                
                switch (item.getServicePoint().toUpperCase()) {
                    case "LABORATORY":
                        data.setLab(data.getLab() + item.getAmount());
                        break;
                    case "PHARMACY":
                        data.setPharmacy(data.getPharmacy() + item.getAmount());
                        break;
                    case "PROCEDURE":
                    case "TRIAGE":
                        data.setProcedure(data.getProcedure() + item.getAmount());
                        break;
                    case "RADIOLOGY":
                        data.setRadiology(data.getRadiology() + item.getAmount());
                        break;
                    case "CONSULTATION":
                        data.setConsultation(data.getConsultation() + item.getAmount());
                        break;
                    case "COPAYMENT":
                        data.setCopay(data.getCopay() + item.getAmount());
                        break;
                    default:
                        data.setOther(data.getOther() + item.getAmount());
                        break;
                }
                data.setDiscount(data.getDiscount());
            }
            billData.add(data);
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("createdOn");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField.setName("paymentMode");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        
        reportData.setData(billData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/payment_statement");
        reportData.setReportName("Payement_Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getInvoiceStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        String transactionNo = reportParam.getFirst("transactionNo");
        Long payer = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Long scheme = NumberUtils.createLong(reportParam.getFirst("schemeId"));
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo");
        Boolean awaitingSmart = reportParam.getFirst("awaitingSmart") != null ? Boolean.parseBoolean(reportParam.getFirst("awaitingSmart")) : false;
         Boolean hasCapitation = reportParam.getFirst("hasCapitation") != null ? Boolean.parseBoolean(reportParam.getFirst("hasCapitation")) : false;
        DateRange dateRange = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        String invoiceStatus = reportParam.getFirst("invoiceStatus");
        Double amountGreaterThan = 0.0;
        Boolean filterPastDue = false;
        Double amountLessThanOrEqualTo = 0.0;
        
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        ReportData reportData = new ReportData();
        InvoiceStatus status = invoiceStatusToEnum(invoiceStatus);
        
        List<InvoiceData> invoices = invoiceService.fetchInvoices(payer, scheme, invoiceNo, status, patientNo, dateRange, amountGreaterThan, filterPastDue, awaitingSmart, amountLessThanOrEqualTo, hasCapitation, Pageable.unpaged()).getContent()
                .stream()
                .map((invoice) -> invoice.toData())
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
                        data.setOther(data.getOther() != null ? data.getOther().add(item.getAmount()) : item.getAmount());
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
        reportData.getFilters().put("range", DateRange.getReportPeriod(dateRange));
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_invoice_statement");
        reportData.setReportName("invoice_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void genInsuranceStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        
        Long payer = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Long scheme = NumberUtils.createLong(reportParam.getFirst("schemeId"));
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo");
        String dateRange = reportParam.getFirst("range");
        InvoiceStatus status = invoiceStatusToEnum(reportParam.getFirst("invoiceStatus"));
        Boolean awaitingSmart = reportParam.getFirst("awaitingSmart") != null ? Boolean.parseBoolean(reportParam.getFirst("awaitingSmart")) : null;
        Boolean hasCapitation = reportParam.getFirst("hasCapitation") != null ? Boolean.parseBoolean(reportParam.getFirst("hasCapitation")) : false;
        Double amountGreaterThan = null;
        Boolean filterPastDue = null;
        Double amountLessThanOrEqualTo = null;
        
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<InvoiceData> invoiceData = invoiceService.fetchInvoices(payer, scheme, invoiceNo, status, patientNo, range, amountGreaterThan, filterPastDue, awaitingSmart, amountLessThanOrEqualTo,hasCapitation, pageable).getContent()
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
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_statement");
        reportData.setReportName("insurance_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getAgingReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        
        Long payer = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Long scheme = NumberUtils.createLong(reportParam.getFirst("schemeId"));
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo");
        String dateRange = reportParam.getFirst("dateRange");
        InvoiceStatus status = invoiceStatusToEnum(reportParam.getFirst("invoiceStatus"));
        Boolean awaitingSmart = reportParam.getFirst("awaitingSmart") != null ? Boolean.parseBoolean(reportParam.getFirst("awaitingSmart")) : null;
        Boolean hasCapitation = reportParam.getFirst("hasCapitation") != null ? Boolean.parseBoolean(reportParam.getFirst("hasCapitation")) : false;
        Double amountGreaterThan = null;
        Boolean filterPastDue = null;
        Double amountLessThanOrEqualTo = null;
        
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<InvoiceData> invoiceData = invoiceService.fetchInvoices(payer, scheme, invoiceNo, status, patientNo, range, amountGreaterThan, filterPastDue, awaitingSmart, amountLessThanOrEqualTo, hasCapitation,pageable).getContent()
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
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/aging_report");
        reportData.setReportName("insurance_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getPatientReceipt(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData(); 
        String receiptNo = reportParam.getFirst("receiptNo");
        //"RCT-00009"
        ReceiptData receiptData = paymentService.getPaymentByReceiptNumber(receiptNo).toData();
        reportData.setData(Arrays.asList(receiptData));
        reportData.setFormat(format);
        reportData.setTemplate("/payments/general_receipt");
        reportData.setReportName("Receipt" + receiptNo);
        reportService.generateReport(reportData, response);
    }
    
    public void getPatientPayments(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String receiptNo = reportParam.getFirst("receiptNo");
        String payee = reportParam.getFirst("payee");
        String transactionNo = reportParam.getFirst("transactionNo");
        Long servicePointId = NumberUtils.createLong(reportParam.getFirst("servicePointId"));
        String shiftNo = reportParam.getFirst("shiftNo");
        Long cashierId = NumberUtils.createLong(reportParam.getFirst("cashierId"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        ReportReceiptData data = null;//new ReportReceiptData();
        //"RCT-00009"
        List<ReportReceiptData> receiptDataArray = new ArrayList();
        List<ReceiptData> receiptData = receivePaymentService.getPayments(payee, receiptNo, transactionNo, shiftNo, servicePointId, cashierId, range, Pageable.unpaged())
                .stream()
                .map((receipt) -> receipt.toData())
                .collect(Collectors.toList());
        for (ReceiptData receipt : receiptData) {
            data = new ReportReceiptData();
            data.setId(receipt.getId());
            data.setPayer(receipt.getPayer());
            data.setDescription(receipt.getDescription());
//            data.setAmount(receipt.getAmount());
            data.setRefundedAmount(receipt.getRefundedAmount());
            data.setTenderedAmount(receipt.getTenderedAmount());
            data.setPaymentMethod(receipt.getPaymentMethod());
            data.setReferenceNumber(receipt.getReferenceNumber());
            data.setTransactionNo(receipt.getTransactionNo());
            data.setReceiptNo(receipt.getReceiptNo());
            data.setCurrency(receipt.getCurrency());
            data.setPaid(receipt.getPaid());
            data.setShiftNo(receipt.getShiftNo());
            data.setTransactionDate(receipt.getTransactionDate());
            data.setCreatedBy(receipt.getCreatedBy());
            for (ReceiptTransactionData trx : receipt.getTransactions()) {
                switch (trx.getMethod().toUpperCase()) {
                    case "BANK":
                        data.setBank(data.getBank().add(trx.getAmount()));
                        break;
                    case "CARD":
                        data.setCard(data.getCard().add(trx.getAmount()));
                        break;
                    case "MOBILE MONEY":
                        data.setMobilemoney(data.getMobilemoney().add(trx.getAmount()));
                        data.setReferenceNumber(trx.getReference());
                        break;
                    case "CASH":
                        data.setCash(data.getCash().add(trx.getAmount()));
                        break;
                    case "DISCOUNT":
                        data.setDiscount(data.getDiscount().add(trx.getAmount()));
                        break;
                    default:
                        data.setOtherPayment(data.getOtherPayment().add(trx.getAmount()));
                        break;
                }
                
            }
            
            for (ReceiptItemData item : receipt.getReceiptItems()) {
                switch (item.getServicePoint().toUpperCase()) {
                    case "LABORATORY":
                        data.setLab(data.getLab().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    case "PHARMACY":
                        data.setPharmacy(data.getPharmacy().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    case "PROCEDURE":
                    case "TRIAGE":
                        data.setProcedure(data.getProcedure().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    case "RADIOLOGY":
                        data.setRadiology(data.getRadiology().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    case "CONSULTATION":
                        data.setConsultation(data.getConsultation().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    case "COPAYMENT":
                        data.setCopayment(data.getCopayment().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                        break;
                    default:
                        data.setOther(data.getOther() != null ? data.getOther().add(item.getAmountPaid()) : item.getAmountPaid());
                        
                        break;
                }
                data.setAmount(data.getAmount().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                data.setDiscount(data.getDiscount().add(NumberUtils.toScaledBigDecimal(item.getDiscount())));
            }
            receiptDataArray.add(data);
        }
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("transactionDate");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(receiptDataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/departmental_mode_report");
        reportData.setReportName("Departmental-Payment-Stetment");
        reportService.generateReport(reportData, response);
    }
    
    public void getDepartmentalPayments(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long servicePointId = NumberUtils.createLong(reportParam.getFirst("servicePointId"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        String receiptNo = reportParam.getFirst("receiptNo");
        String itemCode = reportParam.getFirst("itemCode");
        Boolean voided = reportParam.getFirst("cancelled") == null ? Boolean.parseBoolean(reportParam.getFirst("cancelled")) : null;
        String patientNo = reportParam.getFirst("patientNo");
        //"RCT-00009"
        List<ReceiptItemData> receiptDataArray = receivePaymentService.getVoidedItems(servicePointId, patientNo, itemCode, voided, range, Pageable.unpaged())
                .stream()
                .map((receipt) -> receipt.toData())
                .collect(Collectors.toList());

//        receiptData.forEach((receipt) -> {
//            receiptDataArray.addAll(receipt.getReceiptItems());
//        });
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("servicePoint");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField = new JRDesignSortField();
        sortField.setName("transactionDate");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(receiptDataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/Department_payment_statement");
        reportData.setReportName("Department-Income-Stetment");
        reportService.generateReport(reportData, response);
    }
    
    public void getLedgers(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Boolean includeSubLedgers = reportParam.getFirst("includeSubLedgers") != null ? Boolean.getBoolean(reportParam.getFirst("includeSubLedgers")) : null;
        String term = reportParam.getFirst("term");
        String type = reportParam.getFirst("type");
        
        List<LedgerData> ledgerData = ledgerService.fetchLedgers(includeSubLedgers, term, type, Pageable.unpaged()).getLedgers();
        reportData.setData(ledgerData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/ledger_statement");
        
        reportData.setReportName("ledger-Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getLedger(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String identifier = reportParam.getFirst("identifier");
        LedgerData ledgerData = ledgerService.findLedgerData(identifier).orElseThrow(() -> APIException.notFound("Ledger with id  {0} not found"));;
        
        reportData.setData(Arrays.asList(ledgerData));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/ledger_statement");
        
        reportData.setReportName("Ledger-Report");
        reportService.generateReport(reportData, response);
    }
    
    public void getPatientStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String patientNumber = reportParam.getFirst("patientNumber");
        String visitNumber = reportParam.getFirst("visitNumber");
        String billNumber = reportParam.getFirst("billNumber");
        Long servicePointId = NumberUtils.createLong(reportParam.getFirst("servicePointId"));
        Boolean hasBalance = reportParam.getFirst("hasBalance") != null ? Boolean.getBoolean(reportParam.getFirst("hasBalance")) : null;
        BillStatus status = BillStatusToEnum(reportParam.getFirst("billStatus"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        List<BillItemData> data = billService.getPatientBillItems(patientNumber, visitNumber, billNumber, null, servicePointId, hasBalance, status, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((bill) -> bill.toData())
                .collect(Collectors.toList());
        reportData.setPatientNumber(patientNumber);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(data);
        reportData.setFormat(format);
        reportData.setTemplate("/payments/patient_statement");
        reportData.setReportName("Patient_Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getInterimBill(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        BillDetail data = billService.getBillDetails(visitNumber, Pageable.unpaged());
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(data.getBills());
        reportData.setFormat(format);
        reportData.setTemplate("/payments/interim_bill");
        reportData.setReportName("Interim_Bill" + visitNumber);
        reportService.generateReport(reportData, response);
    }
    
    public void getDispatchStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        
        String dispatchNo = reportParam.getFirst("dispatchNo");
        
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        DispatchData dispatchData = DispatchData.map(dispatchService.getDispatchByNumberWithFailDetection(dispatchNo));
        
        reportData.setData(Arrays.asList(dispatchData));
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/dispatch_note");
        reportData.setReportName("Dispatch-Note");
        reportService.generateReport(reportData, response);
    }
    
    public void getVoidedInvoicesItems(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        
        Long payer = NumberUtils.createLong(reportParam.getFirst("payerId"));
        Long scheme = NumberUtils.createLong(reportParam.getFirst("schemeId"));
        String patientNo = reportParam.getFirst("patientNo");
        String invoiceNo = reportParam.getFirst("invoiceNo");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        InvoiceStatus status = invoiceStatusToEnum(reportParam.getFirst("invoiceStatus"));
        
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<InvoiceItemData> invoiceData = invoiceService.fetchVoidedInvoiceItem(payer, scheme, invoiceNo, status, patientNo, range, Pageable.unpaged()).getContent()
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("invoiceNo");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);        
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/voided_invoice_statement");
        reportData.setReportName("voided_invoice_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getServicesDispensed(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        
        Long servicePointId = NumberUtils.createLong(reportParam.getFirst("servicePointId"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        String receiptNo = reportParam.getFirst("receiptNo");
        String patientNo = reportParam.getFirst("patientNo");
        String itemCode = reportParam.getFirst("itemCode");
        Boolean voided = reportParam.getFirst("cancelled") == null ? null : Boolean.parseBoolean(reportParam.getFirst("cancelled"));
        //"RCT-00009"
        List<ReceiptItemData> receiptDataArray = receivePaymentService.getVoidedItems(servicePointId, patientNo, itemCode, voided, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((receipt) -> receipt.toData())
                .collect(Collectors.toList());
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("servicePoint");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField = new JRDesignSortField();
        sortField.setName("itemCode");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        sortField = new JRDesignSortField();
        sortField.setName("transactionDate");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(receiptDataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/Service_statement");
        reportData.setReportName("Department-Service-Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getAccountsBals(ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
//        MultiValueMap<String, String> reportParam = new Map();
//        Boolean includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;
//        String term = reportParam.getFirst("term");
//        AccountType type = AccountTypeToEnum(reportParam.getFirst("type"));
//        Boolean includeCustomerAccounts = reportParam.getFirst("includeCustomerAccounts") != null ? Boolean.getBoolean(reportParam.getFirst("includeCustomerAccounts")) : null;
        ReportData reportData = new ReportData();
        List<AccountData> accountData = accountService.fetchAccounts(null, null, null, null, Pageable.unpaged()).getAccounts();

//        List<JRSortField> sortList = new ArrayList<>();
//        JRDesignSortField sortField = new JRDesignSortField();
//        sortField.setName("type");
//        sortField.setOrder(SortOrderEnum.ASCENDING);
//        sortField.setType(SortFieldTypeEnum.FIELD);
//        sortList.add(sortField);
//
//        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(accountData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/accounts_bf");
        reportData.setReportName("accounts_Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getPaymentTransactions(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        String method = reportParam.getFirst("method");
        TrnxType trnxtype = EnumUtils.getEnum(TrnxType.class, reportParam.getFirst("trnxType"));
        String receiptNo = reportParam.getFirst("receiptNo");
        //"RCT-00009"
        List<ReceiptTransactionData> receiptDataArray = receivePaymentService.getTransactions(method, receiptNo, trnxtype, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((trans) -> trans.toData())
                .collect(Collectors.toList());
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("receiptNo");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setData(receiptDataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/payment_transactions");
        reportData.setReportName("Payment-Transactions-Statement");
        reportService.generateReport(reportData, response);
    }

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
    
    private AccountType AccountTypeToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(AccountType.class, status)) {
            return AccountType.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Account Type");
    }
    
    private PaymentMethod PaymentMethodToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(PaymentMethod.class, status)) {
            return PaymentMethod.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Payment Method");
    }
    
    private BillStatus BillStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(BillStatus.class, status)) {
            return BillStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
    
}

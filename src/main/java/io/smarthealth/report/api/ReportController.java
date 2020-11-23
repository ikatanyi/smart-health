package io.smarthealth.report.api;

import io.smarthealth.clinical.admission.service.AdmissionService;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.report.domain.enumeration.ReportName;
import io.smarthealth.report.service.LabReportService;
import io.smarthealth.report.service.PatientReportServices;
import io.smarthealth.report.service.RadiologyReportService;
import io.smarthealth.report.service.AccountReportService;
import io.smarthealth.report.service.AdmissionReportService;
import io.smarthealth.report.service.PaymentReportService;
import io.smarthealth.report.service.PharmacyReportService;
import io.smarthealth.report.service.ProcedureReportService;
import io.smarthealth.report.service.StockReportService;
import io.smarthealth.report.service.SupplierReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Api
@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class ReportController {

//    private final JasperReportsService reportService;
    private final AccountReportService reportService;
    private final LabReportService labReportService;
    private final RadiologyReportService radiologyReportService;
    private final PatientReportServices patientReportService;
    private final SupplierReportService supplierInvoiceService;
    private final ProcedureReportService procedureReportService;
    private final PharmacyReportService pharmacyReportService;
    private final PaymentReportService paymentReportService;
    private final StockReportService stockReportService;
    private final AdmissionReportService admissionReportService;

    @GetMapping("/report")
    @PreAuthorize("hasAuthority('view_reports')")
    @ApiOperation(value = "Daily_Income_Statement=[transactionNo,paymentMode,billNo,visitNumber,billStatus, patientNo,hasbalance,dateRange]\n"
            + "genInsuranceStatement=[payerId,schemeId,patientNo,invoiceNo,range,invoiceStatus]\n"
            + "Purchase_Order=[orderNo]")
    public ResponseEntity<?> generateReport(
            @RequestParam(value = "reportName", required = true) ReportName reportName,
            @RequestParam(required = false) MultiValueMap<String, String> queryParams,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        switch (reportName) {
            case Trial_Balance:
                reportService.getTrialBalance(queryParams, format, response);
                break;
            case Daily_Income_Statement:
                reportService.getDailyPayment(queryParams, format, response);
                break;
            case Insurance_Statement:
                reportService.genInsuranceStatement(queryParams, format, response);
                break;
            case Invoice_Statement:
                reportService.getInvoiceStatement(queryParams, format, response);
                break;
            case Aging_Report:
                reportService.getAgingReport(queryParams, format, response);
                break;
            case Invoice:
                paymentReportService.getInvoice(queryParams, format, response);
                break;
            case Patient_File:
                patientReportService.getPatientFile(queryParams, format, response);
                break;
            case Lab_Report:
                labReportService.getPatientLabReport(queryParams, format, response);
                break;
            case Procedure_Summary:
                procedureReportService.getPatientProcedureReport(queryParams, format, response);
                break;
            case Request_Form:
                patientReportService.getPatientRequest(queryParams, format, response);
                break;
            case Prescription:
                pharmacyReportService.getPrescription(queryParams, format, response);
                break;
            case Radiology_Report:
                radiologyReportService.getPatientRadiolgyReport(queryParams, format, response);
                break;
            case Sick_Off_Note:
                patientReportService.getSickOff(queryParams, format, response);
                break;
            case Specimen_Label:
                labReportService.genSpecimenLabel(queryParams, format, response);
                break;
            case Prescription_Label:
                pharmacyReportService.getPrescriptionLabel(queryParams, format, response);
                break;
            case Lab_Statement_Summarized:
                labReportService.getLabTestStatement(queryParams, format, response);
                break;
            case Lab_Statement:
                labReportService.getLabTestStatement(queryParams, format, response);
                break;
            case Radiology_Studies_Summary:
                radiologyReportService.getRadiologyStatement(queryParams, format, response);
                break;
            case Patient_List:
                patientReportService.getPatients(queryParams, format, response);
                break;
            case Patient_Card:
                patientReportService.getPatientCard(queryParams, format, response);
                break;
            case Patient_Visit:
                patientReportService.getVisit(queryParams, format, response);
                break;
            case Patient_Diagnosis:
                patientReportService.getDiagnosis(queryParams, format, response);
                break;
            case Doctor_Invoice:
                supplierInvoiceService.getDoctorInvoiceStatement(queryParams, format, response);
                break;
            case Supplier_Invoice:
                supplierInvoiceService.SupplierInvoiceStatement(queryParams, format, response);
                break;
            case Supplier_Aging_Report:
                supplierInvoiceService.SupplierAgingReport(queryParams, format, response);
                break;
            case Patient_Receipt:
                reportService.getPatientReceipt(queryParams, format, response);
                break;
            case Accounts:
                reportService.getAccounts(queryParams, format, response);
                break;
            case Income_Expense_Account:
                reportService.getIncomeExpenseAccounts(format, response);
                break;
            case Chart_Of_Account:
                reportService.getChartOfAccounts(format, response);
                break;
            case Balance_Sheet:
                reportService.getBalanceSheet(queryParams, format, response);
                break;
            case Income_Statement:
                reportService.getIncomeStatement(queryParams, format, response);
                break;
            case Petty_Cash_Form:
                paymentReportService.getPettyCash(queryParams, format, response);
                break;
            case Petty_Cash_statement:
                paymentReportService.getPettyCashRequests(queryParams, format, response);
                break;
            case Payment_Voucher:
                paymentReportService.getPaymentVoucher(queryParams, format, response);
                break;
//            case Payment_Statement:
//                paymentReportService.getPaymentStatement(queryParams, format, response);
//                break;
            case Ledger_Report:
                reportService.getLedger(queryParams, format, response);
                break;
//            case Ledger_statement:
//                reportService.getLedgers(queryParams, format, response);
//                break;
            case Journal_Report:
                reportService.getJournal(queryParams, format, response);
                break;
            case Payer_Credit_Note:
                paymentReportService.getcreditNote(queryParams, format, response);
                break;
            case Supplier_Credit_Note:
                stockReportService.getSuppliercreditNote(queryParams, format, response);
                break;
            case Purchase_Order:
                stockReportService.getPurchaseOrder(queryParams, format, response);
                break;
            case Account_Transactions:
                reportService.getAccTransactions(queryParams, format, response);
                break;
            case Remittance_Report:
                reportService.getRemittanceReport(queryParams, format, response);
                break;
            case Allocation_Report:
                reportService.getAllocationReport(queryParams, format, response);
                break;
            case Stock_Inventory:
                stockReportService.getInventoryItems(queryParams, format, response);
                break;
            case Stock_Adjustment:
                stockReportService.getInventoryAdjustedItems(queryParams, format, response);
                break;
            case Referral_Form:
                patientReportService.getReferralForm(queryParams, format, response);
                break;
            case Medical_Report:
                patientReportService.getVisitNote(queryParams, format, response);
                break;
            case Appointment_Letter:
                patientReportService.getAppointmentLetter(queryParams, format, response);
                break;
            case General_Collection_Statement:
                reportService.getPatientPayments(queryParams, format, response);
                break;
            case Service_Point_Income_Statement:
                reportService.getServicePointIncomeStatement(queryParams, format, response);
                break;
            case Goods_Receive_Note:
                stockReportService.SupplierGRN(queryParams, format, response);
                break;
            case Inventory_Stock:
                stockReportService.InventoryStock(queryParams, format, response);
                break;
            case Shift_Report:
                paymentReportService.shiftPayments(queryParams, format, response);
                break;
            case Patient_Statement:
                reportService.getPatientStatement(queryParams, format, response);
                break;
            case Dispatch_Note:
                reportService.getDispatchStatement(queryParams, format, response);
                break;
            case Interim_Bill:
                reportService.getInterimBill(queryParams, format, response);
                break;
            case Expiry_Report:
                stockReportService.InventoryExpiryStock(queryParams, format, response);
                break;
            case Voided_Invoice_Statement:
                reportService.getVoidedInvoicesItems(queryParams, format, response);
                break;
            case Service_Statement:
                reportService.getServicesDispensed(queryParams, format, response);
                break;
            case Product_Statement:
                stockReportService.getItems(queryParams, format, response);
                break;
            case Morbidity_Report:
                patientReportService.getMorbidityReport(queryParams, format, response);
                break;
            case OP_Statement:
                patientReportService.getMohOPAttendanceReport(queryParams, format, response);
                break;
            case Patient_Register:
                patientReportService.getPatientRegisterReport(queryParams, format, response);
                break;
            case Dispensed_Drugs:
                pharmacyReportService.DispenseReport(queryParams, format, response);
                break;
            case Patient_Dispensed_Drugs:
                pharmacyReportService.getPatientDispensedDrugs(queryParams, format, response);
                break;
            case Stock_Purchase_Report:
                stockReportService.StockPurchase(queryParams, format, response);
                break;
            case Payment_Transactions_Statement:
                reportService.getPaymentTransactions(queryParams, format, response);
                break;
            case Registered_Schemes:
                reportService.getSchemeConfig(queryParams, format, response);
                break;    
            case Insurance_Diagnosis_Summary:
                reportService.genDiagnosisStatement(queryParams, format, response);
                break;
            case Cashier_Cash_Sale_Breakdown:
                paymentReportService.getCashierShift(queryParams, format, response);
                break;
            case Patient_Attendance_Report:
                patientReportService.getPatientReport(queryParams, format, response);
                break;
            case Invoice_Aging_Summary:
                reportService.getAgingSummary(queryParams, format, response);
                break;  
            case Purchase_Order_Statement:
                stockReportService.getPurchaseOrderStatement(queryParams, format, response);
                break;
            case Moh706_Lab_Report:
                patientReportService.getMoh706LabReport(queryParams, format, response);
                break;
            case Invoice_Balancing_Statement:
                reportService.genInvoiceBalancingReport(queryParams, format, response);
                break;
            default:
                break;
        }
        return ResponseEntity.ok("success");
    }

    @GetMapping("/report/clinical/prescription-label/{prescriptionId}")
    public void generatePrescriptionLabel(
            @PathVariable Long prescriptionId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {

    }

}

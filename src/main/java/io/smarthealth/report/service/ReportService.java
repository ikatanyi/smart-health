/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalance;
import io.smarthealth.accounting.accounts.service.TrialBalanceService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.clinical.lab.data.PatientTestRegisterData;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.clinical.lab.service.LabSetupService;
import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.data.SickOffNoteData;
import io.smarthealth.clinical.record.domain.PatientNotes;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.accounts.DailyBillingData;
import io.smarthealth.report.data.accounts.InsuranceInvoiceData;
import io.smarthealth.report.data.accounts.InvoiceData;
import io.smarthealth.report.data.accounts.InvoiceItemData;
import io.smarthealth.report.data.accounts.TrialBalanceData;
import io.smarthealth.report.data.clinical.PatientVisitData;
import io.smarthealth.report.data.clinical.specimenLabelData;
import java.io.IOException;
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
    private final TrialBalanceService trialBalanceService;
    private final BillingService billService; 
    private final InvoiceService invoiceService; 
    private final VisitService visitService;
    private final PatientService patientService;
    private final RadiologyService radiologyService;
    private final ProcedureService procedureService;
    private final LabService labService;
    private final DiagnosisService diagnosisService;
    private final PatientNotesService patientNotesService;
    private final PharmacyService pharmacyService;
    private final DoctorRequestService doctorRequestService;
    private final PrescriptionService prescriptionService;
    private final SickOffNoteService sickOffNoteService;
    private final LabSetupService labSetUpService;
    

    public void getTrialBalance(final boolean includeEmptyEntries, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        List<TrialBalanceData> dataList = new ArrayList();
        ReportData reportData = new ReportData();
        TrialBalance trialBalance = trialBalanceService.getTrialBalance(includeEmptyEntries);

        trialBalance.getTrialBalanceEntries().stream().map((trialBalEntry) -> {
            TrialBalanceData data = new TrialBalanceData();
            data.setCreditTotal(trialBalance.getCreditTotal());
            data.setDebitTotal(trialBalance.getDebitTotal());
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
        }).forEachOrdered((data) -> {
            dataList.add(data);
        });
        reportData.setData(dataList);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/TrialBalance");
        reportData.setReportName("trialBalance");
        reportService.generateReport(reportData, response);
    }

    public void getDailyPayment(String transactionNo, String visitNo, String patientNo, String paymentMode, String billNo, String dateRange, String billStatus, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
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
        
        reportData.setData(billData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/payment_statement");
        reportData.setReportName("payement_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void getInvoiceStatement(Long payer, Long scheme, String invoiceNo, String patientNo, String dateRange, String invoiceStatus, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        ReportData reportData = new ReportData();
        InvoiceStatus status = invoiceStatusToEnum(invoiceStatus);
        
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
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
        reportData.setData(invoiceData);reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_invoice_statement");
        reportData.setReportName("invoice_statement");
        reportService.generateReport(reportData, response);
    }
    
    public void genInsuranceStatement(Long payer, Long scheme, String invoiceNo, String dateRange, String patientNo, ExportFormat format, HttpServletResponse response) throws SQLException, IOException, JRException {
        List<InsuranceInvoiceData> invoiceData = new ArrayList();
        ReportData reportData = new ReportData();
        Map<String, Object> map = reportData.getFilters();
        InvoiceStatus status = invoiceStatusToEnum(String.valueOf(map.get("billStatus")));
        DateRange  range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
        for (Invoice invoice : invoices) {
            InsuranceInvoiceData data = new InsuranceInvoiceData();
            data.setAmount(invoice.getTotal());
            data.setBalance(invoice.getBalance());
            data.setDiscount(invoice.getDisounts());
            if(invoice.getBill()!=null){
                data.setPatientId(invoice.getBill().getPatient().getPatientNumber());
                data.setPatientName(invoice.getBill().getPatient().getFullName());
            }
            data.setDueDate(String.valueOf(invoice.getDueDate()));
            data.setInvoiceNo(invoice.getNumber());
            if(invoice.getPayer()!=null)
                data.setPayer(invoice.getPayer().getPayerName());
            if(invoice.getPayee()!=null)    
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
        reportData.setData(invoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/accounts/insurance_statement");
        reportData.setReportName("insurance_statement");
        reportService.generateReport(reportData, response);
    }
    
     public void getInvoice(String transactionNo, Long payer, Long scheme, String patientNo, String invoiceNo, String dateRange, String invoiceStatus, ExportFormat format,  HttpServletResponse response) throws SQLException, JRException, IOException {
        List<InvoiceData> invoiceData = new ArrayList();
        ReportData reportData = new ReportData();
        InvoiceStatus status = invoiceStatusToEnum(invoiceStatus);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Invoice> invoices = invoiceService.fetchInvoices(payer, scheme, invoiceNo, dateRange, patientNo, range, pageable).getContent();
        
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
        reportData.setData(invoiceData);
        reportData.setTemplate("/accounts/invoice");
        reportData.setReportName("invoice");
        reportData.setFormat(format);
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
     public void getPatientFile(final String PatientId, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException{
         List<PatientVisitData>visitData = new ArrayList();
         PatientVisitData patientVisitData = new PatientVisitData();
         PatientData patient = patientService.convertToPatientData(patientService.findPatientOrThrow(PatientId));
         if(!patient.getAddress().isEmpty()){
            patientVisitData.setAddressCountry(patient.getAddress().get(0).getCountry());
            patientVisitData.setAddressCounty(patient.getAddress().get(0).getCounty());
            patientVisitData.setAddressLine1(patient.getAddress().get(0).getLine1());
            patientVisitData.setAddressLine2(patient.getAddress().get(0).getLine2());
            patientVisitData.setAddressPostalCode(patient.getAddress().get(0).getPostalCode());
            patientVisitData.setAddressTown(patient.getAddress().get(0).getTown());
         }
         if(!patient.getContact().isEmpty()){
            patientVisitData.setContactEmail(patient.getContact().get(0).getEmail());
            patientVisitData.setContactMobile(patient.getContact().get(0).getMobile());
            patientVisitData.setContactTelephone(patient.getContact().get(0).getTelephone());
         }
         patientVisitData.setDateOfBirth(String.valueOf(patient.getDateOfBirth()));
         patientVisitData.setFullName(patient.getFullName());
         patientVisitData.setGender(String.valueOf(patient.getGender()));
         patientVisitData.setTitle(patient.getTitle());
         patientVisitData.setPatientId(patient.getPatientNumber());         
         Pageable pageable = PaginationUtil.createPage(1, 500);
         List<Visit>visits = visitService.fetchVisitByPatientNumber(PatientId, pageable).getContent();
         if(visits.isEmpty())
             visitData.add(patientVisitData);
         for(Visit visit:visits){
             PatientVisitData pVisitData = patientVisitData;
             List<PatientScanRegisterData> scanData= radiologyService.findPatientScanRegisterByVisit(visit)
                                                .stream()
                                                .map((scan)->PatientScanRegisterData.map(scan))
                                                .collect(Collectors.toList());
            List<PatientProcedureRegisterData> procedures = procedureService.findPatientProcedureRegisterByVisit(visit.getVisitNumber())
                                                            .stream()
                                                            .map((proc)->PatientProcedureRegisterData.map(proc))
                                                            .collect(Collectors.toList());
            
            List<PatientTestRegisterData> labTests = labService.findPatientTestRegisterByVisit(visit)
                                                      .stream()
                                                      .map((test)->PatientTestRegisterData.map(test))
                                                      .collect(Collectors.toList());
            
            Optional<PatientNotes> patientNotes = patientNotesService.fetchPatientNotesByVisit(visit);
            if(patientNotes.isPresent()){
                PatientNotes notes=patientNotes.get();
                pVisitData.setBriefNotes(notes.getBriefNotes());
                pVisitData.setChiefComplaint(notes.getChiefComplaint());
                pVisitData.setExaminationNotes(notes.getExaminationNotes());
                pVisitData.setHistoryNotes(notes.getHistoryNotes());
                
            }
            
             List<DiagnosisData> diagnosisData = diagnosisService.fetchAllDiagnosisByVisit(visit, pageable)
                                                  .stream()
                                                  .map((diag)->DiagnosisData.map(diag))
                                                  .collect(Collectors.toList());
             List<PatientDrugsData> pharmacyData = pharmacyService.getByVisitIdAndPatientId(visit.getVisitNumber(), PatientId);
             
             pVisitData.setVisitNumber(visit.getVisitNumber());
             pVisitData.setCreatedOn(String.valueOf(visit.getCreatedOn()));
             pVisitData.setLabTests(labTests);             
             pVisitData.setProcedures(procedures);
             pVisitData.setRadiologyTests(scanData);
             pVisitData.setDrugsData(pharmacyData);
             pVisitData.setDiagnosis(diagnosisData);
             pVisitData.setAge(patient.getAge());
             pVisitData.setPractitionerName(visit.getHealthProvider().getFullName());
             
             visitData.add(pVisitData);
         }
         
        List<JRSortField> sortList = new ArrayList();
        ReportData reportData = new ReportData();
        reportData.setPatientNumber(PatientId);
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(visitData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/patientFile");
        reportData.setReportName("Patient-file");
        reportService.generateReport(reportData, response);
        
                  
     }
     
     public void getPatientRequest(String visitNumber, RequestType requestType, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<DoctorRequestData> requestData = doctorRequestService.findAllRequestsByVisitAndRequestType(visit, requestType, pageable)
                                                      .getContent()
                                                      .stream()
                                                      .map((test)->DoctorRequestData.map(test))
                                                      .collect(Collectors.toList());
        
         reportData.setPatientNumber(visit.getPatient().getPatientNumber());
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
           
         }
        reportData.getFilters().put("SUBREPORT_DIR", "/clinical/");
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/request_form");
        reportData.setReportName(requestType.name()+"_request_form");
        reportService.generateReport(reportData, response);
    }
     
     public void getPatientLabReport(String visitNumber, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientTestRegisterData> labTests = labService.findPatientTestRegisterByVisit(visit)
                                                      .stream()
                                                      .map((test)->PatientTestRegisterData.map(test))
                                                      .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(labTests);
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());           
         }
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/patientLab_report");
        reportData.setReportName("Lab-report");
        reportService.generateReport(reportData, response);
    }
     
    public void getPatientProcedureReport(String visitNumber, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientProcedureRegisterData> procTests = procedureService.findPatientProcedureRegisterByVisit(visitNumber)
                                                      .stream()
                                                      .map((test)->PatientProcedureRegisterData.map(test))
                                                      .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(procTests);
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());           
         }
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/patient_procedure_report");
        reportData.setReportName("procedure-report");
        reportService.generateReport(reportData, response);
    } 
    
    public void getPatientRadiologyReport(String visitNumber, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientScanRegisterData> scans = radiologyService.findPatientScanRegisterByVisit(visit)
                                                      .stream()
                                                      .map((test)->PatientScanRegisterData.map(test))
                                                      .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(scans);
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());           
         }
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/patient_radiology_report");
        reportData.setReportName("radiology-report");
        reportService.generateReport(reportData, response);
    } 
     
     public void getPrescription(String visitNumber, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<PrescriptionData> requestData = prescriptionService.fetchAllPrescriptionsByVisit(visit, pageable)
                                                      .getContent()
                                                      .stream()
                                                      .map((test)->PrescriptionData.map(test))
                                                      .collect(Collectors.toList());

        
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());           
         }
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/prescription");
        reportData.setReportName("prescription");
        reportService.generateReport(reportData, response);
    }
     
    public void getSickOff(String visitNumber, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        
        Visit visit =visitService.findVisitEntityOrThrow(visitNumber);
        List<SickOffNoteData> requestData = Arrays.asList(SickOffNoteData.map(sickOffNoteService.fetchSickNoteByVisitWithNotFoundThrow(visit)));
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
         if(visit.getHealthProvider()!=null){
           reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());           
         }
        reportData.setTemplate("/clinical/sick_off_note");
        reportData.setReportName("sick-off-note");
        reportService.generateReport(reportData, response);
    } 
    
    public void genSpecimenLabel(String patientNumber,Long specimenId, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        specimenLabelData labelData = new specimenLabelData();
        Optional<PatientData> patientData = patientService.fetchPatientByPatientNumber(patientNumber);
        if(patientData.isPresent()){
            labelData.setDateOfBitrh(patientData.get().getDateOfBirth());
            labelData.setPatientName(patientData.get().getPatientNumber()+", "+patientData.get().getFullName());
            
        }
        Specimen specimen = labSetUpService.fetchSpecimenById(specimenId);
        labelData.setSpecimenCode(specimen.getId().toString());
        labelData.setSpecimenName(specimen.getSpecimen());
        
        List<specimenLabelData> requestData = Arrays.asList(labelData);
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/specimen_label");
        reportData.setReportName("specimen-label");
        reportService.generateReport(reportData, response);
    } 
    
    public void getPrescriptionLabel(Long prescriptionId, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        PrescriptionData prescriptionData = null;
        
        Optional<Prescription> prescription = prescriptionService.fetchPrescriptionById(prescriptionId);
        if(prescription.isPresent()){
            prescriptionData = PrescriptionData.map(prescription.get());
            reportData.setPatientNumber(prescriptionData.getPatientNumber());
        }
        reportData.setData(Arrays.asList(prescriptionData));
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/presc_label");
        reportData.setReportName("prescription-label");
        reportService.generateReport(reportData, response);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
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
public class SupplierReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final RadiologyService scanService;
    
    private final SupplierService supplierService;
    private final DoctorInvoiceService doctorInvoiceService;
    private final PurchaseInvoiceService purchaseInvoiceService;
    
    
    public void getSuppliers(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String type= reportParam.getFirst("type"); 
        Boolean includeClosed= Boolean.getBoolean(reportParam.getFirst("includeClose")); 
        String term = reportParam.getFirst("term"); 
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        
         List<SupplierData> patientData = supplierService.getSuppliers(type, true, term, pageable)
                .getContent()
                .stream()
                .map((supplier) -> supplier.toData())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/supplierList");
        reportData.setReportName("supplier-List");
        reportService.generateReport(reportData, response);
    }    
    
    public void getSupplierStatement(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String type= reportParam.getFirst("type"); 
        Boolean includeClosed= Boolean.getBoolean(reportParam.getFirst("includeClose")); 
        String term = reportParam.getFirst("term"); 
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        
         List<SupplierData> patientData = supplierService.getSuppliers(type, true, term, pageable)
                .getContent()
                .stream()
                .map((supplier) -> supplier.toData())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/supplierList");
        reportData.setReportName("supplier-List");
        reportService.generateReport(reportData, response);
    }    
    
    
    public void getDoctorInvoiceStatement(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String type= reportParam.getFirst("type"); 
        Long doctorId = Long.getLong(reportParam.getFirst("doctorId"));
        String serviceItem = reportParam.getFirst("serviceItem");
        Boolean paid = Boolean.valueOf(reportParam.getFirst("paid"));
        String paymentMode = reportParam.getFirst("paymentMode"); 
        String patientNo = reportParam.getFirst("patientNo"); 
        String invoiceNumber = reportParam.getFirst("invoiceNumber"); 
        String transactionId = reportParam.getFirst("transactionId"); 
        String dateRange = reportParam.getFirst("range"); 
        
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        
         List<DoctorInvoiceData> doctorInvoiceData = doctorInvoiceService.getDoctorInvoices(doctorId, serviceItem, paid, paymentMode, patientNo, invoiceNumber, transactionId, range, pageable)
                .getContent()
                .stream()
                .map((invoice) -> invoice.toData())
                .collect(Collectors.toList());
        reportData.setData(doctorInvoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/DoctorInvoiceStatement");
        reportData.setReportName("Doctor-invoice-statement");
        reportService.generateReport(reportData, response);
    }    
    
    public void DoctorInvoice(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long id = Long.getLong(reportParam.getFirst("id"));     
        
        DoctorInvoiceData doctorInvoiceData = doctorInvoiceService.getDoctorInvoice(id).toData();
        reportData.setData(Arrays.asList(doctorInvoiceData));
        reportData.setFormat(format);        
        reportData.setEmployeeId(doctorInvoiceData.getStaffNumber());
       
        reportData.setTemplate("/supplier/DoctorInvoice");
        reportData.setReportName("Doctor-invoice-statement");
        reportService.generateReport(reportData, response);
    }    
    
    public void SupplierInvoiceStatement(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long supplierId = Long.getLong(reportParam.getFirst("supplierId"));  
        String invoiceNumber = reportParam.getFirst("supplierId");
        Boolean paid = reportParam.getFirst("paid")!=null?Boolean.getBoolean(reportParam.getFirst("paid")):null;
        PurchaseInvoiceStatus status = PurchaseInvoiceStatusToEnum(reportParam.getFirst("status"));
        String dateRange = reportParam.getFirst("range"); 
        
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        Pageable pageable = PaginationUtil.createPage(page, size);
        
        List<PurchaseInvoiceData> purchaseInvoiceData = purchaseInvoiceService.getSupplierInvoices(supplierId, invoiceNumber, paid, status, pageable)
                .getContent()
                .stream()
                .map((invoice) -> invoice.toData())
                .collect(Collectors.toList());
        reportData.setData(purchaseInvoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/SupplierInvoiceStatement");
        reportData.setReportName("Supplier-invoice-statement");
        reportService.generateReport(reportData, response);
    } 
    
    public void SupplierInvoice(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long id = Long.valueOf(reportParam.getFirst("id"));  
        
        PurchaseInvoiceData purchaseInvoiceData = purchaseInvoiceService.findOneWithNoFoundDetection(id).toData();
        if (purchaseInvoiceData.getSupplier()!= null) {
            reportData.setSupplierId(purchaseInvoiceData.getSupplierId());
        }       
        reportData.setData(Arrays.asList(purchaseInvoiceData));
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/supplierInvoice");
        reportData.setReportName("Supplier-invoice");
        reportService.generateReport(reportData, response);
    }    
    
    private PurchaseInvoiceStatus PurchaseInvoiceStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(PurchaseInvoiceStatus.class, status)) {
            return PurchaseInvoiceStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid PurchaseInvoice Status");
    }
   
}

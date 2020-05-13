/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.payer.data.PayerData;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.EnglishNumberToWords;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteData;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.smarthealth.stock.purchase.service.PurchaseService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class StockReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final RadiologyService scanService;
    
    private final SupplierService supplierService;
    private final DoctorInvoiceService doctorInvoiceService;
    private final PurchaseInvoiceService purchaseInvoiceService;
    private final PurchaseService purchaseService;
    
    
    public void getSuppliers(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String type= reportParam.getFirst("type"); 
        Boolean includeClosed= Boolean.getBoolean(reportParam.getFirst("includeClosed")); 
        String term = reportParam.getFirst("term"); 
        
         List<SupplierData> patientData = supplierService.getSuppliers(type, true, term, Pageable.unpaged())
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
    
    public void getSuppliercreditNote(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String creditNoteNo = reportParam.getFirst("creditNoteNo");

        PurchaseCreditNoteData creditNoteData = purchaseInvoiceService.findByNumberWithNoFoundDetection(creditNoteNo).toData();

        reportData.getFilters().put("category", "Supplier");
        Optional<Supplier> supplier = supplierService.getSupplierById(creditNoteData.getSupplierId());
           if(supplier.isPresent()){
               reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));
               
           }
        
        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(creditNoteData.getAmount()).toUpperCase());    
        reportData.setData(Arrays.asList(creditNoteData));
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/supplier_credit_note");
        reportData.setReportName("Supplier-Credit-Note"+creditNoteNo);
        reportService.generateReport(reportData, response);
    }
    
    public void getPurchaseOrder(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String orderNo = reportParam.getFirst("orderNo");

        PurchaseOrderData purchaseOrderData = purchaseService.findByOrderNumberOrThrow(orderNo).toData();

        reportData.getFilters().put("category", "Supplier");
        Optional<Supplier> supplier = supplierService.getSupplierById(purchaseOrderData.getSupplierId());
           if(supplier.isPresent()){
               reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));
               
           }
        
        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(purchaseOrderData.getPurchaseAmount()).toUpperCase());    
        reportData.setData(Arrays.asList(purchaseOrderData));
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/purchase_order");
        reportData.setReportName("Purchase-Order"+orderNo);
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

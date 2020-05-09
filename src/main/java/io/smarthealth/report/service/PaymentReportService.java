/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.accounting.payment.service.PaymentService;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private final VisitService visitService;

    public void getPaymentVoucher(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String voucherNo = reportParam.getFirst("voucherNo");

        PaymentData paymentData = paymentService.getPaymentByVoucherNo(voucherNo).toData();

        reportData.setData(Arrays.asList(paymentData));
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/payment/payment_voucher");
        reportData.setReportName("Payment-voucher");
        reportService.generateReport(reportData, response);
    }
    
    public void getPaymentStatement(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
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
        reportData.setTemplate("/clinical/payment/payment_statement");
        
        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("transactionNo");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setReportName("Payment-Statement");
        reportService.generateReport(reportData, response);
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

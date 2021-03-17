package io.smarthealth.report.experiments;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.AbstractXlsReportConfiguration;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportRepository reportRepository;
    private final JasperReportsService jasperReportService;

    //    public ReportExportService(ReportRepository reportRepository) {
//        this.reportRepository = reportRepository;
//    }
    public void exportPDFReport(String template, Map<String, Object> parameters, OutputStream outputStream) throws Exception {
        JasperPrint jasperPrint = reportRepository.generateJasperPrint(template, parameters);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

    }

    public void exportReport(String template, ExportFormat format, Map<String, Object> parameters, OutputStream outputStream) throws Exception {
        JasperPrint jasperPrint = reportRepository.generateJasperPrint(template, parameters);
    }

    public void testExport(String clientId, OutputStream outputStream) throws JRException, IOException, SQLException {
        JasperPrint jasperPrint = reportRepository.testExport(clientId);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

    public void walkingRegister(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, FileNotFoundException, JRException, IOException {
        String template = "/patient/WalkIn.jrxml";
        String reportName = "Walking Report";
        Map<String, Object> params = new HashMap();

        DateRange dateRange = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));
        String date = null;
        if (reportParam.getFirst("range") != null) {
            date = reportParam.getFirst("range");
        } else {
            throw APIException.badRequest("Please provide date range");
        }
        List<String> dateList = Arrays.asList(date.replace("..", ",").split(","));

        params.put("Date_From", LocalDate.parse(dateList.get(0)).atStartOfDay().toString());
        params.put("Date_To", LocalDate.parse(dateList.get(1)).atTime(LocalTime.MAX).toString());
        Exporter exporter = new JRPdfExporter();
        JasperPrint jasperPrint = reportRepository.generateJasperPrint(template, params);
        jasperReportService.export(jasperPrint, format, template, response);
    }

    public void pricelistSummary(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, FileNotFoundException, JRException, IOException {
        String template = "/inventory/ProductsPriceList.jrxml";
        String reportName = "Pricelist Summary";
        Map<String, Object> params = new HashMap();

        String conditions = "";
        if (reportParam.getFirst("itemCode") != null) {
            if (!conditions.equals("")) {
                conditions = "AND ".concat(conditions);
            }
            conditions = " item_code = '" + reportParam.getFirst("itemCode") + "' ";
        }
        if (reportParam.getFirst("type") != null) {
            if (!conditions.equals("")) {
                conditions = "AND ".concat(conditions);
            }
            conditions = " type = '" + reportParam.getFirst("type") + "' ";
        }
        if (reportParam.getFirst("category") != null) {
            if (!conditions.equals("")) {
                conditions = "AND ".concat(conditions);
            }
            conditions = " category = '" + reportParam.getFirst("category") + "' ";
        }
        if (!conditions.equals("")) {
            conditions = " WHERE ".concat(conditions);
        }
        params.put("CONDITIONS", conditions);
        Exporter exporter = new JRPdfExporter();
        JasperPrint jasperPrint = reportRepository.generateJasperPrint(template, params);
        jasperReportService.export(jasperPrint, format, template, response);
    }

}

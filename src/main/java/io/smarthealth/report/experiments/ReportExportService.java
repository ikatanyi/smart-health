/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.experiments;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
public class ReportExportService {

    private final ReportRepository reportRepository;

    public ReportExportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

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

}

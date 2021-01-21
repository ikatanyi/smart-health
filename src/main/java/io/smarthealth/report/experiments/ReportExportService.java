/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.experiments;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
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

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportRepository reportRepository;
//    private final JasperReportsService jasperReportService;

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
        String template = "/Patient/WalkingRegister.jrxml";
        String reportName = "Walking Report";
        Map<String, Object> params = new HashMap();
//        Exporter exporter = new JRPdfExporter();
//        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + "pdf".toLowerCase()));
//
//        OutputStream out = response.getOutputStream();
//        JasperPrint jasperPrint = reportRepository.generateJasperPrint(template, params);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
//        export(jasperPrint, format, template, response);
    }

    public void export(final JasperPrint jprint, ExportFormat type, String reportName, HttpServletResponse response) throws JRException, IOException {
        final Exporter exporter;
        final OutputStream out = response.getOutputStream();
        SimpleOutputStreamExporterOutput exporterOutput = null;
        boolean html = false;

        switch (type) {
            case HTML:
                exporter = new HtmlExporter();
                response.setContentType("text/html");
                final SimpleHtmlReportConfiguration configuration = new SimpleHtmlReportConfiguration();

                SimpleHtmlExporterOutput htmlOutput = new SimpleHtmlExporterOutput(out);
                htmlOutput.setImageHandler(new WebHtmlResourceHandler("/jasper_images?image={0}"));

                configuration.setIgnorePageMargins(true);
//                configuration.setSizeUnit(POINT);

                // Or try this instead of setSizeUnit(POINT)...
                configuration.setZoomRatio(2.0f);
                exporter.setConfiguration(configuration);
                exporter.setExporterOutput(htmlOutput);
//                exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + type.name().toLowerCase()));
                break;

            case CSV:
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + type.name().toLowerCase()));
                break;

            case XML:
                exporter = new JRXmlExporter();
                response.setContentType("text/xml");
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + type.name().toLowerCase()));
                break;

            case XLS:
            case XLSX:
                AbstractXlsReportConfiguration config = null;
                if (type.name().toLowerCase().equals("xlsx")) {
                    exporter = new JRXlsxExporter();
                    config = new SimpleXlsxReportConfiguration();
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                } else {
                    exporter = new JRXlsExporter();
                    config = new SimpleXlsxReportConfiguration();
                    response.setContentType("application/vnd.ms-excel");
                }

                config.setOnePagePerSheet(false);
                config.setIgnoreGraphics(Boolean.TRUE);
//                config.setDetectCellType(Boolean.TRUE);
                config.setRemoveEmptySpaceBetweenRows(Boolean.FALSE);
                config.setCollapseRowSpan(Boolean.TRUE);
                config.setIgnoreGraphics(Boolean.TRUE);
                config.setWrapText(Boolean.TRUE);
                config.setColumnWidthRatio(2.0F);
                config.setShowGridLines(Boolean.TRUE);
                config.setDetectCellType(Boolean.TRUE);
//                config.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
                config.setFontSizeFixEnabled(false);
                config.setSheetNames(new String[]{"Sheet1"});
                // exporter.setConfiguration(config);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
//                File outputFile = new File("excelTest.xlsx");
//                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));

                response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + type.name().toLowerCase()));
                break;

            case PDF:
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", String.format("attachment; filename=" + reportName + "." + type.name().toLowerCase()));
                break;

            default:
                throw new JRException("Unknown report format: " + type);
        }

//        if (!type.name().equalsIgnoreCase("HTML") && !type.name().equalsIgnoreCase("CSV")) {
//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
//        }
//        exporterOutput = new SimpleOutputStreamExporterOutput(out);
        SimpleExporterInput exporterInput = new SimpleExporterInput(jprint);
        exporter.setExporterInput(exporterInput);
        exporter.exportReport();
    }

}

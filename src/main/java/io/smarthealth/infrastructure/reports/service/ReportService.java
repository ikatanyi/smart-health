/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.reports.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class ReportService {

;
JasperPrint jasperPrint;    
    
 @Autowired
 @Qualifier("jdbcTemplate")
 private JdbcTemplate jdbcTemplate;
 
 private HashMap jasperParameter;
 
 
    public void report_viewer2(String reportname, HashMap m, String type, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // set header as pdf
        
        if (type.equalsIgnoreCase("PDF")) {
            response.setContentType("application/pdf");
//            response.addHeader("Content-disposition", "attachment; filename="+reportname+".pdf");
        }
        if (type.equalsIgnoreCase("XLS")) {
            response.setContentType("application/vnd.ms-excel");
//            response.addHeader("Content-disposition", "attachment; filename="+reportname+".xls");
        }
        if (type.equalsIgnoreCase("CSV")) {
            response.setContentType("text/plain");
//            response.addHeader("Content-disposition", "attachment; filename="+reportname+".csv");
        }
        
        if (type.equalsIgnoreCase("HTML")) {
            response.setContentType("text/html");
//            response.setHeader("Content-disposition", "inline");
        }
        if (type.equalsIgnoreCase("")) {
//            response.setContentType("text/html");
//            response.setHeader("Content-disposition", "inline");
               response.setContentType("application/pdf");
//            response.addHeader("Content-disposition", "attachment; filename="+reportname+".pdf");
        }

        ServletOutputStream servletOutputStream = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // get report            
            ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{});
            Resource resource = appContext.getResource("classpath:com/smart/reports/" + reportname);
            final InputStream rept = resource.getInputStream();
            jasperParameter.putAll(m);
            jasperParameter.put("SUBREPORT_DIR", "com/smart/reports/");
            
//             export to pdf
//            JasperExportManager.exportReportToPdfStream(rpt.displayReport(), baos);
            // export to html
//             byte [] aux = exportReportToHtmlStream(rpt.displayReport(), "");
//             servletOutputStream.write(aux);
            jasperPrint = genJasperPrint(rept, jasperParameter);
            baos = this.exportManager(jasperPrint, type);
            response.setContentLength(baos.size());
            baos.writeTo(servletOutputStream);
//            export(rpt.displayReport(), "PDF").writeTo(servletOutputStream);

        } catch (BeansException | IOException | JRException ex) {
            ex.getMessage();
        } finally {
            baos.close();
        }
    }
    
    public void reportDatasource(String reportname, HashMap m, String type, List list, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // set header as pdf
        response.setContentType("application/pdf");

        // set input and output stream
        ServletOutputStream servletOutputStream = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            // get report            
            ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{});
            Resource resource = appContext.getResource("classpath:com/smart/reports/" + reportname);
            final InputStream rept = resource.getInputStream();
//            jasperParameter.putAll(m);
//            jasperParameter.put("SUBREPORT_DIR", "com/reports/templates/");
//            reports rpt = new reports(jasperParameter, rept, beanDataSource);
            
            // export to pdf
//            JasperExportManager.exportReportToPdfStream(rpt.beanReporter(), baos);
            jasperPrint = this.beanReporter(reportname, m, list);
            baos = this.exportManager(jasperPrint, type);
            response.setContentLength(baos.size());
            baos.writeTo(servletOutputStream);

        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            servletOutputStream.flush();
            servletOutputStream.close();
            baos.close();
        }
    }
    
    public ByteArrayOutputStream exportManager(final JasperPrint print, String type) throws JRException {
        final Exporter exporter;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean html = false;

        switch (type) {
            case "HTML":                
                exporter = new HtmlExporter();                
                html = true;
                break;

            case "CSV":
                exporter = new JRCsvExporter();
                break;

            case "XML":
                exporter = new JRXmlExporter();
                break;

            case "XLSX":
                exporter = new JRXlsxExporter();
                break;

            case "PDF":
                exporter = new JRPdfExporter();
                break;

            default:
                throw new JRException("Unknown report format: " + type);
        }

//        if (!html) {
//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
//        }
          exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));

//        exporter.setExporterInput(new SimpleExporterInput(print));
//        exporter.exportReport();

        return out;
    }
    
//    public void savePdfReport(String reportName) {
//        try {
//            Connection conn = jdbcTemplate.getDataSource().getConnection();
//            ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{});
//            Resource resource = appContext.getResource("classpath:com/smart/reports/" + reportName);
//            final InputStream jasperReport = resource.getInputStream();
//            this.jasperPrint = JasperFillManager.fillReport(jasperReport, this.jasperParameter, conn);
//            this.pdf(jasperParameter.get("name").toString());
//
//            if (!conn.isClosed()) {
//                conn.close();
//            }
//        } catch (JRException ex) {
//            JOptionPane.showMessageDialog(null, ex.getMessage() + ex.getMessage());
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, ex.getMessage() + ex.getMessage());
//        } finally {
//            session.close();
//        }
//    }

    public JasperPrint beanReporter(String reportname, HashMap m, List list) throws IOException {
        try {
            ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{});
            Resource resource = appContext.getResource("classpath:com/smart/reports/" + reportname);
            JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(list);
            final InputStream jasperReport = resource.getInputStream();
             jasperPrint = JasperFillManager.fillReport(jasperReport, this.jasperParameter, beanDataSource);
        } catch (JRException ex) {
           ex.printStackTrace();
        }
        return this.jasperPrint;
    }
    
    public JasperPrint genJasperPrint(InputStream jasperReport, HashMap jasperParameter) throws JRException {
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
            jasperReportsContext.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
            jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
            jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameter, conn);
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.getMessage();
        } 
        return jasperPrint;
    }
    
}

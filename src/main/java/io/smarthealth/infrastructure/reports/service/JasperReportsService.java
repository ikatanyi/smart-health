package io.smarthealth.infrastructure.reports.service;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.ApplicationProperties;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.clinical.EmployeeBanner;
import io.smarthealth.report.data.clinical.Footer;
import io.smarthealth.report.data.clinical.PatientBanner;
import io.smarthealth.report.data.clinical.Header;
import io.smarthealth.report.storage.StorageService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.AbstractXlsReportConfiguration;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleCsvReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kelsas
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JasperReportsService {

    @Autowired
    private final StorageService storageService;

    @Autowired
    private final FacilityService facilityService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ApplicationProperties appProperties;

    private final PatientService patientService;
    private final EmployeeService employeeService;
    private final SupplierService supplierService;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public byte[] generatePDFReport(String inputFileName, Map<String, Object> params) {
        return generatePDFReport(ExportFormat.PDF, inputFileName, params, new JREmptyDataSource());
    }

    public byte[] generatePDFReport(ExportFormat format, String inputFileName, Map<String, Object> params) {
        return generatePDFReport(format, inputFileName, params, new JREmptyDataSource());
    }

    public byte[] generatePDFReport(ExportFormat format, String inputFileName, Map<String, Object> params,
            JRDataSource dataSource) {
        byte[] bytes = null;
        JasperReport jasperReport = null;
        try {
            // Check if a compiled report exists
            if (storageService.jasperFileExists(inputFileName)) {
                jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(inputFileName));
            } // Compile report from source and save
            else {
                String jrxml = storageService.loadJrxmlFile(inputFileName);
                log.info("{} loaded. Compiling report", jrxml);
                jasperReport = JasperCompileManager.compileReport(jrxml);
                // Save compiled report. Compiled report is loaded next time
                JRSaver.saveObject(jasperReport, storageService.loadJasperFile(inputFileName));
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            if (format == ExportFormat.PDF) {
                bytes = generatePDF(jasperPrint);
            } else if (format == ExportFormat.XLSX) {
                bytes = generateExcel(jasperPrint);
            }
        } catch (JRException e) {
            log.error("Encountered error when loading jasper file", e);
        }

        return bytes;
    }

    private byte[] generatePDF(JasperPrint jasperPrint) throws JRException {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateExcel(JasperPrint jasperPrint) throws JRException {
        byte[] bytes = null;
        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
            SimpleOutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(byteArray);
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(input);
            exporter.setExporterOutput(output);
            exporter.exportReport();
            bytes = byteArray.toByteArray();
            output.close();

        } catch (IOException e) {
            log.error("IO error encountered", e);
        }
        return bytes;
    }

    public byte[] generateEmailReport(ReportData reportData) throws SQLException, JRException, IOException {
        JRDataSource ds = new JRBeanCollectionDataSource(reportData.getData());
        Resource report = resourceLoader.getResource(appProperties.getReportLoc() + reportData.getTemplate() + ".jasper");//new ClassPathResource("static/jasper/rpt_report.jasper");

        HashMap param = reportConfig(null, null,null);
        param.putAll(reportData.getFilters());
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(report.getInputStream(), param, ds);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
//        DataSource aAttachment = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
        return baos.toByteArray();
    }

    public void generateReport(ReportData reportData, HttpServletResponse response) throws SQLException, JRException, IOException {

        JasperPrint jasperPrint = null;
        ExportFormat format = reportData.getFormat();
        if (format == null) {
            format = ExportFormat.PDF;
        }
        List dataList = reportData.getData();
        String template = reportData.getTemplate();
        String patientNumber = reportData.getPatientNumber();
        String employeeId = reportData.getEmployeeId();
        String reportName = reportData.getReportName();
        Long supplierId = reportData.getSupplierId();
        JasperReport jasperReport = null;
        HashMap param = reportConfig(patientNumber, employeeId, supplierId);
        InputStream reportInputStream = resourceLoader.getResource(appProperties.getReportLoc() + template + ".jasper").getInputStream();
        LocalDateTime startTime = LocalDateTime.now();
        // Check if a compiled report exists
        if (reportInputStream != null) {
            jasperReport = (JasperReport) JRLoader.loadObject(reportInputStream);

        } // Compile report from source and save
        else {
            reportInputStream = resourceLoader.getResource(appProperties.getReportLoc() + template + ".jrxml").getInputStream();
            String jrxml = storageService.loadJrxmlFile(resourceLoader.getResource(appProperties.getReportLoc() + template + ".jrxml").getFile().getAbsolutePath());
            jasperReport = JasperCompileManager.compileReport(jrxml);
        }
        // Get your data source
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(dataList, false);

        // Add parameters
//            param.putAll(param);
        param.putAll(reportData.getFilters());
        // Fill the report
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");

//        if (dataList.isEmpty()) {
//            Connection conn = jdbcTemplate.getDataSource().getConnection();
//            jasperPrint = JasperFillManager.fillReport(jasperReport, param, conn);
//        } else {
        jasperPrint = JasperFillManager.fillReport(jasperReport, param, jrBeanCollectionDataSource);

//        }
        System.out.println("Report generated in" + ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()) + "ms");
        export(jasperPrint, format, reportName, response);

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
                    config = new SimpleXlsReportConfiguration();
                    response.setContentType("application/vnd.ms-excel");
                }
                config.setOnePagePerSheet(false);
                config.setIgnoreGraphics(Boolean.TRUE);
                config.setDetectCellType(Boolean.TRUE);
                config.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                config.setCollapseRowSpan(Boolean.TRUE);
//                config.setIgnoreGraphics(Boolean.TRUE);
                config.setWrapText(Boolean.FALSE);
                config.setColumnWidthRatio(2.0F);
                config.setWhitePageBackground(Boolean.FALSE);
                config.setShowGridLines(Boolean.TRUE); 
                config.setDetectCellType(Boolean.TRUE);
                config.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
                config.setFontSizeFixEnabled(Boolean.TRUE);
                config.setSheetNames(new String[]{"Sheet1"});
//                config.setIgnoreCellBackground(Boolean.TRUE);
                config.setIgnoreCellBorder(Boolean.FALSE);
//                config.setCollapseRowSpan(Boolean.FALSE);
                config.setIgnoreGraphics(Boolean.TRUE);
                exporter.setConfiguration(config);
                
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

    /**
     *
     */
    @Transactional(readOnly = true)
    private HashMap reportConfig(String patientNumber, String staffNumber, Long supplierId) throws JRException {
        List<PatientBanner> patientDataArray = new ArrayList();
        List<EmployeeBanner> employeeDataArray = new ArrayList();
        List<Header> header = new ArrayList();
        HashMap jasperParameter = new HashMap();
        SupplierData supplierData = null;

        Facility facility = facilityService.loggedFacility();

        Header headerData = Header.map(facility);
        Footer footerData = Footer.map(facility);
        if (facility.getCompanyLogo()==null) {
            headerData.setIMAGE(new ByteArrayInputStream((appProperties.getReportLoc() + "/logo.png").getBytes()));
            jasperParameter.put("IMAGE_DIR", new ByteArrayInputStream((appProperties.getReportLoc() + "/logo.png").getBytes()));
        } else {
            headerData.setIMAGE(new ByteArrayInputStream(facility.getCompanyLogo().getData()));
            jasperParameter.put("IMAGE_DIR", new ByteArrayInputStream(facility.getCompanyLogo().getData()));
        }

        header.add(headerData);
        jasperParameter.put("Header_Data", header);
        jasperParameter.put("Footer_Data", Arrays.asList(footerData));
        jasperParameter.put("SUBREPORT_DIR", appProperties.getReportLoc() + "/subreports/");
        jasperParameter.put("PIC_DIR", appProperties.getReportLoc() + "/");

        jasperParameter.put("facilityName", facility.getFacilityName());
        jasperParameter.put("facilityType", facility.getFacilityType());
         jasperParameter.put("facilityCode", facility.getRegistrationNumber());
        if (facility.getCompanyLogo() != null) {
            jasperParameter.put("logo", facility.getCompanyLogo().getData());

        }
        jasperParameter.put("orgLegalName", facility.getOrganization().getLegalName());
        jasperParameter.put("orgName", facility.getOrganization().getOrganizationName());
        jasperParameter.put("TaxNumber", facility.getOrganization().getTaxNumber());
        jasperParameter.put("orgWebsite", facility.getOrganization().getWebsite());
        String country = "", county = "", addressLine1 = "", addressLine2 = "", postalcode = "", town = "";
        if (facility.getOrganization().getAddress() != null) {
            for (Address address : facility.getOrganization().getAddress()) {
                jasperParameter.put("orgAddressCountry", country.concat(" ").concat(address.getCountry()));
                jasperParameter.put("orgAddressCounty", county.concat(" ").concat(address.getCounty()));
                jasperParameter.put("orgAddressLine1", addressLine1.concat(" ").concat(address.getLine1()));
                jasperParameter.put("orgAddressLine2", addressLine2.concat(" ").concat(address.getLine2()));
                jasperParameter.put("orgPostalCode", postalcode.concat(" ").concat(address.getPostalCode()));
                jasperParameter.put("orgTown", town.concat(" ").concat(address.getTown()));
                jasperParameter.put("orgType", address.getType());
            }
        }
        String email = "", fullname = "", mobile = "", salutation = "", telephone = "";
        if (facility.getOrganization().getContact() != null) {
            for (Contact contact : facility.getOrganization().getContact()) {
                jasperParameter.put("contactEmail", email.concat(" ").concat(contact.getEmail()));
                jasperParameter.put("contactFullName", fullname.concat(" ").concat(contact.getFullName()));
                jasperParameter.put("contactMobile", mobile.concat(" ").concat(contact.getMobile()));
                jasperParameter.put("salutation", salutation.concat(" ").concat(contact.getSalutation()));
                jasperParameter.put("telephone", telephone.concat(" ").concat(contact.getTelephone()));
            }
        }

        if (patientNumber != null) {
            PatientBanner patient = null;
            Optional<PatientData> patientData = patientService.fetchPatientByPatientNumber(patientNumber);
            if (patientData.isPresent()) {
                patientDataArray.add(PatientBanner.map(patientData.get()));
            }
        }

        if (staffNumber != null) {
            EmployeeBanner employeeData = null;
            Optional<Employee> employee = employeeService.findEmployeeByStaffNumber(staffNumber);
            if (employee.isPresent()) {
                employeeDataArray.add(EmployeeBanner.map(employeeService.convertEmployeeEntityToEmployeeData(employee.get())));
            }
        }

        if (supplierId != null) {
            Optional<Supplier> supplier = supplierService.getSupplierById(supplierId);
            if (supplier.isPresent()) {
                supplierData = supplier.get().toData();
            }
        }

        JRSimpleTemplate template = new JRSimpleTemplate();
        JRStyle s = new JRBaseStyle("bannerStyle");
        s.setHorizontalImageAlign(HorizontalImageAlignEnum.RIGHT);
        template.addStyle(s);

        s = new JRBaseStyle("headerStyle");
        s.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        s.setFontSize(18.0F);
        s.setBackcolor(Color.RED);
        template.addStyle(s);

        ArrayList templateList = new ArrayList();
        templateList.add(template);

        jasperParameter.put(JRParameter.REPORT_TEMPLATES, templateList);
        jasperParameter.put("Patient_Data", patientDataArray);
        jasperParameter.put("Employee_Data", employeeDataArray);
        jasperParameter.put("Supplier_Data", Arrays.asList(supplierData));

        return jasperParameter;
    }

    public String generateReportHtml(ReportData reportData) throws SQLException, JRException, IOException {

        List dataList = reportData.getData();
        String template = reportData.getTemplate();
        String patientNumber = reportData.getPatientNumber();
        String employeeId = reportData.getEmployeeId();
        Long supplierId = reportData.getSupplierId();
        HashMap param = reportConfig(patientNumber, employeeId, supplierId);
        InputStream reportInputStream = resourceLoader.getResource(appProperties.getReportLoc() + template + ".jasper").getInputStream();

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportInputStream);
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(dataList, false);
        param.putAll(reportData.getFilters());
        // Fill the report
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, jrBeanCollectionDataSource);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        Exporter exporter = new HtmlExporter();
        SimpleHtmlReportConfiguration configuration = new SimpleHtmlReportConfiguration();
        configuration.setIgnorePageMargins(true);
        configuration.setZoomRatio(1.8f);
        exporter.setConfiguration(configuration);
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.exportReport();

        byte[] bytes = out.toByteArray();
        return new String(bytes);
    }

    @Bean
    public ServletRegistrationBean<ImageServlet> imageServlet() {
        ServletRegistrationBean<ImageServlet> servRegBean = new ServletRegistrationBean<>();
        servRegBean.setServlet(new ImageServlet());
        servRegBean.addUrlMappings("/jasper_images");
        servRegBean.setLoadOnStartup(1);
        return servRegBean;
    }
}

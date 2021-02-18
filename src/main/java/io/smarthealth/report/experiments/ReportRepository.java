/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.experiments;

import io.smarthealth.ApplicationProperties;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.report.data.clinical.Footer;
import io.smarthealth.report.data.clinical.Header;
import io.smarthealth.report.storage.StorageService;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationProperties appProperties;
    private final ResourceLoader resourceLoader;
    private final StorageService storageService;
    private final FacilityService facilityService;

//    public ReportRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
    public JasperPrint generateJasperPrint(String template, Map<String, Object> parameters) throws SQLException, FileNotFoundException, JRException {
        try {
            /*Begin of header details */
            List<Header> header = new ArrayList();
            Facility facility = facilityService.loggedFacility();

            Header headerData = Header.map(facility);
            Footer footerData = Footer.map(facility);
            if (facility.getCompanyLogo()==null) {
                headerData.setIMAGE(new ByteArrayInputStream((appProperties.getReportLoc() + "/logo.png").getBytes()));
                parameters.put("IMAGE_DIR", new ByteArrayInputStream((appProperties.getReportLoc() + "/logo.png").getBytes()));
            } else {
                headerData.setIMAGE(new ByteArrayInputStream(facility.getCompanyLogo().getData()));
                parameters.put("IMAGE_DIR", new ByteArrayInputStream(facility.getCompanyLogo().getData()));
            }

            header.add(headerData);
            parameters.put("Header_Data", header);
            /*End of header details */

            /*
            Begin: Subreport base directory
             */
            parameters.put("SUBREPORT_DIR", appProperties.getReportLoc().concat("/subreports/"));
            /*
            End: Subreport base directory
             */
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            String  jrxml = resourceLoader.getResource(appProperties.getReportLoc() + template).getFile().getAbsolutePath();
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            return jasperPrint;
        } catch (SQLException | JRException | IOException exception) {
            log.error("Error  {} ", exception.getMessage());
            exception.printStackTrace();
            throw APIException.internalError(exception.getMessage());
//            return null;
        }
    }

    public JasperPrint generateJasperPrint(String template, Map<String, Object> parameters, JRBeanCollectionDataSource dataSource) throws FileNotFoundException, JRException {
        try {

            File file = ResourceUtils.getFile("classpath:reports:" + template);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return jasperPrint;

        } catch (FileNotFoundException | JRException exception) {
            log.error("Error {} ", exception.getMessage());
            return null;
        }
    }

    public JasperPrint testExport(String clientId) throws SQLException, FileNotFoundException, JRException {
        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            File file = ResourceUtils.getFile("classpath:reports:test.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", clientId);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            return jasperPrint;

        } catch (FileNotFoundException | SQLException | JRException exception) {
            log.error("Error  {} ", exception.getMessage());
            return null;
        }
    }
}

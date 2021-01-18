/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Repository
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JasperPrint generateJasperPrint(String template, Map<String, Object> parameters) throws SQLException, FileNotFoundException, JRException {
        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            File file = ResourceUtils.getFile("classpath:reports:" + template);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            return jasperPrint;

        } catch (FileNotFoundException | SQLException | JRException exception) {
            log.error("Error  {} ", exception.getMessage());
            return null;
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

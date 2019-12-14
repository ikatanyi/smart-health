package io.smarthealth.report.service;

import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author Kelsas
 *
 */
public interface ReportService {

    /**
     * Generates a HTML report with the given input file. Uses a
     * JREmptyDataSource
     *
     * @param inputFileName report source file without extension
     * @param params report parameters
     * @return the byte[] containing the PDF
     */
    byte[] generatePDFReport(String inputFileName, Map<String, Object> params);

    /**
     * Generates a HTML report with the given input file. Uses a
     * JREmptyDataSource
     *
     * @param format format can be either 'PDF' or 'XLSX'
     * @param inputFileName report source file without extension
     * @param params report parameters
     * @return the byte[] containing the PDF
     */
    byte[] generatePDFReport(ExportFormat format, String inputFileName, Map<String, Object> params);

    /**
     * Generates a HTML report with the given input file
     *
     * @param format format can be either 'PDF' or 'XLSX'
     * @param inputFileName report source file without extension
     * @param params report parameters
     * @param dataSource the source of data
     * @return the byte[] containing the PDF
     */
    byte[] generatePDFReport(ExportFormat format, String inputFileName, Map<String, Object> params, JRDataSource dataSource);
}

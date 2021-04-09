package io.smarthealth.stat.service;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import net.sf.jasperreports.engine.JRDataSource;

import java.util.Map;

public interface ReportService {

    byte[] generateReport(ExportFormat format, String inputFileName, Map<String, Object> params);

    byte[] generateReport(ExportFormat format, String inputFileName, Map<String, Object> params, JRDataSource dataSource);
}

package io.smarthealth.report.spi;

import io.smarthealth.report.domain.ReportDefinition;
import io.smarthealth.report.domain.ReportPage;
import io.smarthealth.report.domain.ReportRequest;

public interface ReportSpecification {

  ReportDefinition getReportDefinition();

  ReportPage generateReport(final ReportRequest reportRequest, int pageIndex, int size);

  void validate(final ReportRequest reportRequest) throws IllegalArgumentException;
}

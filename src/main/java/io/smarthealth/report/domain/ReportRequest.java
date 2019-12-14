package io.smarthealth.report.domain;

import java.util.List;

public class ReportRequest {

  private List<QueryParameter> queryParameters;
  private List<DisplayableField> displayableFields;

  public ReportRequest() {
    super();
  }

  public List<QueryParameter> getQueryParameters() {
    return this.queryParameters;
  }

  public void setQueryParameters(final List<QueryParameter> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public List<DisplayableField> getDisplayableFields() {
    return this.displayableFields;
  }

  public void setDisplayableFields(final List<DisplayableField> displayableFields) {
    this.displayableFields = displayableFields;
  }
}

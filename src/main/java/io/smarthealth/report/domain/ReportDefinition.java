package io.smarthealth.report.domain;

import java.util.List;

public class ReportDefinition {

  private String identifier;
  private String name;
  private String description;
  private List<QueryParameter> queryParameters;
  private List<DisplayableField> displayableFields;

  public ReportDefinition() {
    super();
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
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

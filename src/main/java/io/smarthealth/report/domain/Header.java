package io.smarthealth.report.domain;

import java.util.List;

public class Header {

  private List<String> columnNames;

  public Header() {
    super();
  }

  public List<String> getColumnNames() {
    return this.columnNames;
  }

  public void setColumnNames(final List<String> columnNames) {
    this.columnNames = columnNames;
  }
}

package io.smarthealth.report.domain;

import java.util.List;

public class Row {

  private List<Value> values;

  public Row() {
    super();
  }

  public List<Value> getValues() {
    return this.values;
  }

  public void setValues(final List<Value> values) {
    this.values = values;
  }
}

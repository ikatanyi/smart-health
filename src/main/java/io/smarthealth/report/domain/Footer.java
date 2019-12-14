package io.smarthealth.report.domain;

import java.util.List;

public class Footer {

  private List<Value> values;

  public Footer() {
    super();
  }

  public List<Value> getValues() {
    return this.values;
  }

  public void setValues(final List<Value> values) {
    this.values = values;
  }
}

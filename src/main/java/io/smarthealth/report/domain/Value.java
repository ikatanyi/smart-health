package io.smarthealth.report.domain;

public class Value {

  private String[] values;
  private Type type;

  public Value() {
    super();
  }

  public String[] getValues() {
    return this.values;
  }

  public void setValues(final String[] values) {
    this.values = values;
  }

  public Type getType() {
    return this.type;
  }

  public void setType(final Type type) {
    this.type = type;
  }
}

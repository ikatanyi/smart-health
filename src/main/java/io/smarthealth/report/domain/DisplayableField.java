package io.smarthealth.report.domain;

public class DisplayableField {

  private String name;
  private Type type;
  private Boolean mandatory;

  public DisplayableField() {
    super();
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Type getType() {
    return this.type;
  }

  public void setType(final Type type) {
    this.type = type;
  }

  public Boolean getMandatory() {
    return this.mandatory;
  }

  public void setMandatory(final Boolean mandatory) {
    this.mandatory = mandatory;
  }
}

package io.smarthealth.report.domain;

public class QueryParameter {

  public enum Operator {
    EQUALS,
    IN,
    LIKE,
    BETWEEN,
    GREATER,
    LESSER
  }

  private String name;
  private Type type;
  private Operator operator;
  private String value;
  private Boolean mandatory;
  private AutoCompleteResource autoCompleteResource;

  public QueryParameter() {
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

  public Operator getOperator() {
    return this.operator;
  }

  public void setOperator(final Operator operator) {
    this.operator = operator;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public Boolean getMandatory() {
    return this.mandatory;
  }

  public void setMandatory(final Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public AutoCompleteResource getAutoCompleteResource() {
    return this.autoCompleteResource;
  }

  public void setAutoCompleteResource(final AutoCompleteResource autoCompleteResource) {
    this.autoCompleteResource = autoCompleteResource;
  }
}

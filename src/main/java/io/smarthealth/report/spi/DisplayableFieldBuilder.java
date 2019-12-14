package io.smarthealth.report.spi;

import io.smarthealth.report.domain.DisplayableField;
import io.smarthealth.report.domain.Type;

public class DisplayableFieldBuilder {

  private String name;
  private Type type;
  private Boolean mandatory;

  private DisplayableFieldBuilder(final String name, final Type type) {
    super();
    this.name = name;
    this.type = type;
  }

  public static DisplayableFieldBuilder create(final String name, final Type type) {
    return new DisplayableFieldBuilder(name, type);
  }

  public DisplayableFieldBuilder mandatory() {
    this.mandatory = Boolean.TRUE;
    return this;
  }

  public DisplayableField build() {
    final DisplayableField displayableField = new DisplayableField();
    displayableField.setName(this.name);
    displayableField.setType(this.type);
    displayableField.setMandatory(this.mandatory != null ? this.mandatory : Boolean.FALSE);
    return displayableField;
  }
}

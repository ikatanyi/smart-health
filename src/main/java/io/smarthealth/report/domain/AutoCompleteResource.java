package io.smarthealth.report.domain;


import java.util.List;

public class AutoCompleteResource {

  private String path;
  private List<String> terms;

  public AutoCompleteResource() {
    super();
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public List<String> getTerms() {
    return this.terms;
  }

  public void setTerms(final List<String> terms) {
    this.terms = terms;
  }
}

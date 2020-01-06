package io.smarthealth.report.domain;

import java.util.List;

public class ReportPage {

  private String name;
  private String description;
  private String generatedOn;
  private String generatedBy;
  private Header header;
  private List<Row> rows;
  private Footer footer;
  private boolean hasMore;

  public ReportPage() {
    super();
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

  public String getGeneratedOn() {
    return this.generatedOn;
  }

  public void setGeneratedOn(final String generatedOn) {
    this.generatedOn = generatedOn;
  }

  public String getGeneratedBy() {
    return this.generatedBy;
  }

  public void setGeneratedBy(final String generatedBy) {
    this.generatedBy = generatedBy;
  }

  public Header getHeader() {
    return this.header;
  }

  public void setHeader(final Header header) {
    this.header = header;
  }

  public List<Row> getRows() {
    return this.rows;
  }

  public void setRows(final List<Row> rows) {
    this.rows = rows;
  }

  public Footer getFooter() {
    return this.footer;
  }

  public void setFooter(final Footer footer) {
    this.footer = footer;
  }

  public void setHasMore(final boolean hasMore) {
    this.hasMore = hasMore;
  }

  public boolean isHasMore() {
    return hasMore;
  }
}

package io.smarthealth.report.spi;
import io.smarthealth.report.domain.AutoCompleteResource;
import io.smarthealth.report.domain.QueryParameter;
import io.smarthealth.report.domain.Type;
import java.util.Arrays;

public class QueryParameterBuilder {

  private String name;
  private Type type;
  private QueryParameter.Operator operator;
  private Boolean mandatory;
  private AutoCompleteResource autoCompleteResource;

  private QueryParameter queryParameter;

  private QueryParameterBuilder(final String name, final Type type) {
    super();
    this.name = name;
    this.type = type;
  }

  public static QueryParameterBuilder create(final String name, final Type type) {
    return new QueryParameterBuilder(name, type);
  }

  public QueryParameterBuilder operator(final QueryParameter.Operator operator) {
    this.operator = operator;
    return this;
  }

  public QueryParameterBuilder mandatory() {
    this.mandatory = Boolean.TRUE;
    return this;
  }

  public QueryParameterBuilder autoComplete(final String path, final String... terms) {
    final AutoCompleteResource autoCompleteResource = new AutoCompleteResource();
    autoCompleteResource.setPath(path);
    autoCompleteResource.setTerms(Arrays.asList(terms));
    this.autoCompleteResource = autoCompleteResource;
    return this;
  }

  public QueryParameter build() {
    final QueryParameter queryParameter = new QueryParameter();
    queryParameter.setName(this.name);
    queryParameter.setType(this.type);
    queryParameter.setOperator(this.operator != null ? this.operator : QueryParameter.Operator.EQUALS);
    queryParameter.setMandatory(this.mandatory != null ? this.mandatory : Boolean.FALSE);
    queryParameter.setAutoCompleteResource(this.autoCompleteResource);
    return queryParameter;
  }
}

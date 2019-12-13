package io.smarthealth.report.provider;


import io.smarthealth.report.domain.ReportDefinition;
import io.smarthealth.report.spi.Report;
import io.smarthealth.report.spi.ReportSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReportSpecificationProvider implements ApplicationContextAware {

  private final HashMap<String, ReportSpecification> reportSpecificationCache = new HashMap<>();
  private final HashMap<String, List<ReportDefinition>> reportCategoryCache = new HashMap<>();

  private ApplicationContext applicationContext;

  @Autowired
  public ReportSpecificationProvider() {
    super();
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.initialize();
  }

  public List<String> getAvailableCategories() {
    return new ArrayList<>(this.reportCategoryCache.keySet());
  }

  public List<ReportDefinition> getAvailableReports(final String category) {
    this.log.debug("Looking up report definitions for category {}.", category);
    return this.reportCategoryCache.getOrDefault(category, Collections.emptyList());
  }

  public Optional<ReportSpecification> getReportSpecification(final String category, final String identifier) {
    final String keyForReportSpecificationCache = this.buildKeyForSpecificationCache(category, identifier);
    this.log.debug("Looking up report specification for {}.", keyForReportSpecificationCache);
    return Optional.ofNullable(this.reportSpecificationCache.get(keyForReportSpecificationCache));
  }

  private void initialize() {
    final Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(Report.class);

    beansWithAnnotation.values().forEach(bean -> {
      final ReportSpecification reportSpecification = ReportSpecification.class.cast(bean);
      final Report report = reportSpecification.getClass().getAnnotation(Report.class);
      final String keyForReportSpecificationCache =
          this.buildKeyForSpecificationCache(report.category(), report.identifier());
      this.log.debug("Adding report specification for {}", keyForReportSpecificationCache);

      this.reportCategoryCache.computeIfAbsent(report.category(), (key) -> new ArrayList<>());
      this.reportCategoryCache.get(report.category()).add(reportSpecification.getReportDefinition());
      this.reportSpecificationCache.put(keyForReportSpecificationCache, reportSpecification);
    });
  }

  private String buildKeyForSpecificationCache(final String category, final String identifier) {
    return category + "~" + identifier;
  }

  public Optional<ReportDefinition> findReportDefinition(final String category, final String identifier) {
    final List<ReportDefinition> reportDefinitions = this.reportCategoryCache.get(category);
    if (reportDefinitions != null) {
      return reportDefinitions
          .stream()
          .filter(reportDefinition -> reportDefinition.getIdentifier().equals(identifier))
          .findAny();
    } else {
      return Optional.empty();
    }
  }
}

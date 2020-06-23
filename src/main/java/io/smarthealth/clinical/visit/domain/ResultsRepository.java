package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface ResultsRepository {

    public List<DocResults> getPatientResults(String visitNumber, String patientNumber, DocResults.Type type, DateRange range, String patientName, Employee employee, Boolean showResultsRead);

}

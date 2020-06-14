package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface ResultsRepository {

    public List<DocResults> getPatientResults(String visitNumber, String patientNumber, DocResults.Type type, DateRange range);

}

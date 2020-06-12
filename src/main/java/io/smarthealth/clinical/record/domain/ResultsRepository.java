/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author Kelsas
 */
public interface ResultsRepository {

    public List<DocResults> getPatientResults(DoctorRequestData.RequestType requestType, DateRange range, Pageable pageable);

}

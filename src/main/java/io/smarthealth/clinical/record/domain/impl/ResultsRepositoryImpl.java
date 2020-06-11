/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain.impl;

import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.ResultsRepository;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Repository
@Transactional(readOnly = true)
public class ResultsRepositoryImpl implements ResultsRepository {

    EntityManager em;

    public ResultsRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<DocResults> getPatientResults(DoctorRequestData.RequestType requestType, DateRange range, Pageable pageable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

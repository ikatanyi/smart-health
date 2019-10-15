/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.organization.facility.service.*;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Facility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class AnalyteService {

    @Autowired
    AnalyteRepository AnalyteRepository;

    @Autowired
    ModelMapper modelMapper;

    /*
    a. Create a new department
    b. Read all departments 
    c. Read department by Id
    c. Update department
     */
    @Transactional
    public Analyte createAnalyte(Analyte analyte) {
        return AnalyteRepository.save(analyte);
    }

    public Page<Analyte> fetchAllAnalytes(Pageable pgbl) {
        return AnalyteRepository.findAll(pgbl);
    }
    
    public Page<AnalyteData> fetchAnalyteByTestType(Testtype testtype, Pageable pgbl) {
        return AnalyteRepository.findByTestType(testtype, pgbl).map(p ->convertAnalyteToData(p));
    }
    
    public Analyte fetchAnalyteById(Long id) {
        return AnalyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteById(Long id) {
        AnalyteRepository.deleteById(id);
    }

    public AnalyteData convertAnalyteToData(Analyte analyte) {
        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
        return analyteData;
    }

}

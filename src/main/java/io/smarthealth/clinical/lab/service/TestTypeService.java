/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.TestTypeData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.TestTypeRepository;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author simon.waweru
 */
@Service
public class TestTypeService {

    @Autowired
    TestTypeRepository ttypeRepository;
    
     @Autowired
     AnalyteRepository analyteRepository;
    
    @Autowired
    ModelMapper modelMapper;

    

    @Transactional
    public Long createTestType(TestTypeData testtypeData) {
        try {
            Testtype testtype = TestTypeData.map(testtypeData);
            ttypeRepository.save(testtype);
            return testtype.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
        }
    }
    
    @Transactional
    public Optional<TestTypeData> getById(Long id) {
        try {
             return ttypeRepository.findById(id).map(d -> convertToTestTypeData(d));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("TestType identified by {0} not found ", e.getMessage());
        }
    }
    
     @Transactional
    public String saveTestType(Testtype testtype) {
        try {
            ttypeRepository.save(testtype);
            return testtype.getServiceCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
        }
    }

    public Testtype fetchTestTypeById(Long testtypeId) {
        return ttypeRepository.findById(testtypeId).orElseThrow(() -> APIException.notFound("TestType identified by {0} not found", testtypeId));
    }
    
    
    public List<AnalyteData> saveAnalytes(ArrayList<Analyte> analyte){
         List<AnalyteData> analyteArray = new ArrayList();
         List<Analyte> analytes = analyteRepository.saveAll(analyte);
         for(Analyte analyt:analytes){
             analyteArray.add(convertToAnalyteData(analyt));
         }
        return analyteArray;
    }
    
    public Testtype fetchTestTypeByCode(String testTypeCode) {
        Testtype ttype = ttypeRepository.findByServiceCode(testTypeCode).orElseThrow(() -> APIException.notFound("TestType identified by code {0} not found", testTypeCode));
//        List analytes = analyteRepository.findByTestType(ttype, PageRequest.of(0, 50)).getContent();
//        if(ttype!=null)
//            ttype.setAnalytes(analytes);
        return ttype;
    }
    
//    public Testtype fetchTestTypeByCodeAndPatient(String testTypeCode, String patientId) {
//        return ttypeRepository.findByServiceCode(testTypeCode).orElseThrow(() -> APIException.notFound("TestType identified by code {0} not found", testTypeCode));
//    }
    
    

    public Page<Testtype> fetchAllTestTypes(Pageable pageable) {
//        ContentPage<TestTypeData> testTypeData = new ContentPage<>();
        List<TestTypeData> testtypeDataList = new ArrayList<>();
        
        Page<Testtype> testtypes = ttypeRepository.findAll(pageable);
//        testTypeData.setTotalElements(testtypes.getTotalElements());
//        testTypeData.setTotalPages(testtypes.getTotalPages());
//        if (testtypes.getSize() > 0) {            
//            for (Testtype testtype : testtypes)
//                testtypeDataList.add(TestTypeData.map(testtype));
//            testTypeData.setContents(testtypeDataList);
//        }
        return testtypes;
    }

    public boolean deleteFacility(TestTypeData testtype) {
        try {
            ttypeRepository.deleteById(testtype.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error deleting TestType id " + testtype.getId(), e.getMessage());
        }
    }
    
    public TestTypeData convertToTestTypeData(Testtype testtype) {
        TestTypeData testtypeData = modelMapper.map(testtype, TestTypeData.class);
        return testtypeData;
    }
    
    public AnalyteData convertToAnalyteData(Analyte analyte) {
        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
        return analyteData;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.TestTypeData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.lab.service.AnalyteService;
import io.smarthealth.clinical.lab.service.TestTypeService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.organization.facility.api.*;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Test Type Controller", description = "Operations pertaining to TestTypes maintenance")
public class TestTypeController {

    @Autowired
    TestTypeService ttypeService;

    @Autowired
    AnalyteService analyteService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/testtype")
    public @ResponseBody
    ResponseEntity<?> createTestType(@RequestBody @Valid final TestTypeData testtypeData) {
        Long id = ttypeService.createTestType(testtypeData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/testtype" + id)
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, testtypeData));
     
    }
    
    
//    @GetMapping("/testtype/{id}")
//    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("id") final Long id) {
//        Optional<TestTypeData> testType = ttypeService.getById(id);
//        if (testType.isPresent()) {
//            return ResponseEntity.ok(testType.get());
//        } else {
//            throw APIException.notFound("TestType Number {0} not found.", id);
//        }
//    }
    
    @GetMapping("/testtype/{code}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("code") final String code) {
        TestTypeData testType = convertToTestTypeData(ttypeService.fetchTestTypeByCode(code));
        if (testType!=null) {
            return ResponseEntity.ok(testType);
        } else {
            throw APIException.notFound("TestType Number {0} not found.", code);
        }
    }

    @GetMapping("/testtype")
    public ResponseEntity<List<TestTypeData>> fetchAllTestTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {

        Page<TestTypeData> page = ttypeService.fetchAllTestTypes(pageable).map(d -> convertToTestTypeData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("/analyte")
    public @ResponseBody
    ResponseEntity<?> createAnalytes(@RequestBody @Valid final TestTypeData testtypeData) {
        Testtype ttype = convertTestTTypeDataToTestType(testtypeData);
        Testtype testtype = ttypeService.fetchTestTypeById(ttype.getId());
        
        ArrayList<Analyte> lists=new ArrayList<>();
        for(Analyte analyte:ttype.getAnalytes()){
            analyte.setTestType(testtype);
            analyte.setTestCode(testtype.getServiceCode());
            analyte.setTestType(testtype); 
            lists.add(analyte);
        }
        
        List<AnalyteData> analytedata = ttypeService.saveAnalytes(lists);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/testtype" + ttype.getServiceCode())
                .buildAndExpand(ttype.getServiceCode()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Analytes successfuly created", HttpStatus.CREATED, analytedata));
    }

    private Testtype convertTestTTypeDataToTestType(TestTypeData testtypeData) {
        Testtype ttype = modelMapper.map(testtypeData, Testtype.class);
        return ttype;
    }

    private TestTypeData convertToTestTypeData(Testtype Testtype) {
        TestTypeData testTypeData = modelMapper.map(Testtype, TestTypeData.class);
        return testTypeData;
    }
    
    
}

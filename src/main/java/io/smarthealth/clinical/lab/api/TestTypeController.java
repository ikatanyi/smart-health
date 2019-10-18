package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.TestTypeData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "Test Type Controller", description = "Operations pertaining to TestTypes maintenance")
public class TestTypeController {

    @Autowired
    LabService labService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/testtype")
    public @ResponseBody
    ResponseEntity<?> createTestType(@RequestBody @Valid final TestTypeData testtypeData) {
        Long id = labService.createTestType(testtypeData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/testtype" + id)
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, testtypeData));
     
    }    
    
    @GetMapping("/testtype/{id}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("id") final Long id) {
        Optional<TestTypeData> testType = labService.getById(id);
        if (testType.isPresent()) {
            return ResponseEntity.ok(testType.get());
        } else {
            throw APIException.notFound("TestType Number {0} not found.", id);
        }
    }
    
    @GetMapping("/analyte/{code}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("code") final String code) {
        TestTypeData testType = convertToTestTypeData(labService.fetchTestTypeByCode(code));
        if (testType!=null) {
            return ResponseEntity.ok(testType);
        } else {
            throw APIException.notFound("TestType Number {0} not found.", code);
        }
    }

    @GetMapping("/testtype")
    public ResponseEntity<List<TestTypeData>> fetchAllTestTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {

        Page<TestTypeData> page = labService.fetchAllTestTypes(pageable).map(d -> convertToTestTypeData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("/analyte")
    public @ResponseBody
    ResponseEntity<?> createAnalytes(@RequestBody @Valid final TestTypeData testtypeData) {
        Testtype ttype = convertTestTTypeDataToTestType(testtypeData);
        Testtype testtype = labService.fetchTestTypeById(ttype.getId());
        
        ArrayList<Analyte> lists=new ArrayList<>();
        for(Analyte analyte:ttype.getAnalytes()){
            analyte.setTestType(testtype);
            analyte.setTestCode(testtype.getServiceCode());
            analyte.setTestType(testtype); 
            lists.add(analyte);
        }
        
        List<AnalyteData> analytedata = labService.saveAnalytes(lists);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/testtype" + ttype.getServiceCode())
                .buildAndExpand(ttype.getServiceCode()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Analytes successfuly created", HttpStatus.CREATED, analytedata));
    }
    
    @DeleteMapping("/analyte/(id)")
    public ResponseEntity<?> deleteTestAnalyte(@PathVariable("id") final Long id) {
        labService.deleteAnalyteById(id);
        return ResponseEntity.ok("200");
    }
    
    @DeleteMapping("/testtype/(id)")
    public ResponseEntity<?> deleteTestType(@PathVariable("id") final Long id) {
        labService.deleteById(id);
        return ResponseEntity.ok("200");
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

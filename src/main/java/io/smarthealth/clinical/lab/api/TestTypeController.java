package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.exception.APIException;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    ResponseEntity<?> createTestType(@RequestBody @Valid final LabTestTypeData testtypeData) {
        Long id = labService.createTestType(testtypeData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/testtype/" + id)
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, testtypeData));
     
    }    
    
    @GetMapping("/testtype/{id}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("id") final Long id) {
        Optional<LabTestTypeData> testType = labService.getById(id);
        if (testType.isPresent()) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/testtype" + id)
                .buildAndExpand(id).toUri();
            return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, testType));
        } else {
            throw APIException.notFound("TestType Number {0} not found.", id);
        }
    }
    
    @GetMapping("/analyte/{code}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("code") final String code) {
        LabTestTypeData testType = convertToTestTypeData(labService.fetchTestTypeByCode(code));
        if (testType!=null) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/analyte" + code)
                .buildAndExpand(code).toUri();
            return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.OK, testType));
        } else {
            throw APIException.notFound("TestType Number {0} not found.", code);
        }
    }

    @GetMapping("/testtype")
    public ResponseEntity<?> fetchAllTestTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {

        Page<LabTestTypeData> page = labService.fetchAllTestTypes(pageable).map(d -> convertToTestTypeData(d));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/testtype")
                .buildAndExpand().toUri();
            return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.OK, page.getContent()));//new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("/analyte")
    public @ResponseBody
    ResponseEntity<?> createAnalytes(@RequestBody @Valid final List<AnalyteData> analyteData) {      
        
        List<AnalyteData> analytedata = labService.saveAnalytes(analyteData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/testtype")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Analytes successfuly created", HttpStatus.CREATED, analytedata));
    }
    
    @DeleteMapping("/analyte/{id}")
    public ResponseEntity<?> deleteTestAnalyte(@PathVariable("id") final Long id) {
        labService.deleteAnalyteById(id);
        return ResponseEntity.ok("200");
    }
    
    @DeleteMapping("/testtype/{id}")
    public ResponseEntity<?> deleteTestType(@PathVariable("id") final Long id) {
        labService.deleteById(id);
        return ResponseEntity.ok("200");
    }
    
    private Testtype convertTestTTypeDataToTestType(LabTestTypeData testtypeData) {
        Testtype ttype = modelMapper.map(testtypeData, Testtype.class);
        return ttype;
    }

    private LabTestTypeData convertToTestTypeData(Testtype Testtype) {
        LabTestTypeData testTypeData = modelMapper.map(Testtype, LabTestTypeData.class);
        return testTypeData;
    }
    //Hope this one works out for now    
}

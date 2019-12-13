package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "Test Type Controller", description = "Operations pertaining to lab tests record(s)")
public class LabTestTypeController {

    private final LabService labService;

    private final ModelMapper modelMapper;

    public LabTestTypeController(LabService labService, ModelMapper modelMapper) {
        this.labService = labService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/test-type")
    public @ResponseBody
    ResponseEntity<?> createTestType(@RequestBody @Valid final LabTestTypeData testtypeData) {
        Long id = labService.createTestType(testtypeData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/test-type/" + id)
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, testtypeData));

    }

    @GetMapping("/test-type/{id}")
    public ResponseEntity<?> fetchAllTestTypes(@PathVariable("id") final Long id) {
        LabTestType testType = labService.findTestById(id);
        return ResponseEntity.ok(convertToTestTypeData(testType));
    }

    @GetMapping("/analyte/{code}")
    public ResponseEntity<?> fetchAllAnnalyetesByTestCode(@PathVariable("code") final String code, Pageable pageable) {
        LabTestType labTest = labService.fetchTestTypeByCode(code);

        Page<AnalyteData> analyteData = labService.fetchAnalyteByTestType(labTest, pageable);

        Pager<List<AnalyteData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(analyteData.getContent());
        PageDetails details = new PageDetails();
        details.setPage(analyteData.getNumber() + 1);
        details.setPerPage(analyteData.getSize());
        details.setTotalElements(analyteData.getTotalElements());
        details.setTotalPage(analyteData.getTotalPages());
        details.setReportName("Analytes for test identified by code " + code);
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/test-type")
    public ResponseEntity<?> fetchAllTests(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<LabTestTypeData> labTestsList = labService.fetchAllTestTypes(pageable).map(d -> convertToTestTypeData(d));
        Pager<List<LabTestTypeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(labTestsList.getContent());
        PageDetails details = new PageDetails();
        details.setPage(labTestsList.getNumber() + 1);
        details.setPerPage(labTestsList.getSize());
        details.setTotalElements(labTestsList.getTotalElements());
        details.setTotalPage(labTestsList.getTotalPages());
        details.setReportName("Lab tests List");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/analyte/{testId}")
    public @ResponseBody
    ResponseEntity<?> createAnalytes(@PathVariable("testId") final Long testId, @RequestBody @Valid final List<AnalyteData> analyteData) {
        LabTestType testType = labService.findTestById(testId);
        List< AnalyteData> analytedata = labService.saveAnalytes(analyteData, testType);
        return ResponseEntity.ok(analytedata);
    }

    @DeleteMapping("/analyte/{id}")
    public ResponseEntity<?> deleteTestAnalyte(@PathVariable("id") final Long id) {
        labService.deleteAnalyteById(id);
        return ResponseEntity.ok("200");
    }

    @DeleteMapping("/testtype/{id}")
    public ResponseEntity<?> deleteTestType(@PathVariable("id") final Long id) {
        labService.deleteTestById(id);
        return ResponseEntity.ok("200");
    }

    private LabTestType convertTestTTypeDataToTestType(LabTestTypeData testtypeData) {
        LabTestType ttype = modelMapper.map(testtypeData, LabTestType.class);
        return ttype;
    }

    private LabTestTypeData convertToTestTypeData(LabTestType Testtype) {
        LabTestTypeData testTypeData = modelMapper.map(Testtype, LabTestTypeData.class);
        return testTypeData;
    }
    //Hope this one works out for now    
}

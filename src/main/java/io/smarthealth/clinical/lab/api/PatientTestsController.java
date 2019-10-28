package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.LabTestData;
import io.smarthealth.clinical.lab.service.LabResultsService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.net.URI;
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
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "Patient Tests Controller", description = "Operations pertaining to Patient lab results maintenance")
public class PatientTestsController {

    @Autowired
    LabResultsService resultService;
    
    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/PatientTest")
    public @ResponseBody
    ResponseEntity<?> createPatientTest(@RequestBody @Valid final LabTestData labTestData) {
        LabTestData Patienttests = resultService.savePatientResults(labTestData);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/PatientTest/" + Patienttests.getId())
                .buildAndExpand(Patienttests.getId()).toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("TestType successfuly created", HttpStatus.CREATED, Patienttests));
     
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
    
    @GetMapping("/PatientTest/{id}")
    public ResponseEntity<?> fetchPatientTestById(@PathVariable("id") final Long id) {
        Optional<LabTestData> result = resultService.fetchPatientTestsById(id);
        if (result!=null) {
            return ResponseEntity.ok(result);
        } else {
            throw APIException.notFound("result Number {0} not found.", id);
        }
    }

    @GetMapping("/result")
    public ResponseEntity<?> fetchAllPatientTests(
             @RequestParam(value = "patientNumber", defaultValue = "") String patientNumber,
             @RequestParam(value = "visitNumber", defaultValue = "") String visitNumber,
             @RequestParam(value = "status", defaultValue = "") String status,
             Pageable pageable
        ) {
        
        Page<LabTestData> pag = resultService.fetchAllPatientTests(patientNumber,visitNumber,status,pageable);
        Pager page = new Pager();
        page.setCode("200");
        page.setContent(pag.getContent());
        page.setMessage("Patient Tests fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Patient Labtests");
        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
        page.setPageDetails(details);
        return ResponseEntity.ok(page);
    }
    
    @DeleteMapping("/result/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        resultService.deleteById(id);
        return ResponseEntity.ok("200");
    }
}

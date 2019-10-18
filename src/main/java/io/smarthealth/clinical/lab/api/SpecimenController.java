package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.swagger.annotations.Api;
import java.util.List;
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
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "specimen Controller", description = "Operations pertaining to Specimen maintenance")
public class SpecimenController {

    @Autowired
    LabService specimenService;
    
    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/specimen")
    public @ResponseBody
    ResponseEntity<?> createSpecimen(@RequestBody @Valid final List<SpecimenData> specimenData) {
        List<SpecimenData> specimenList = specimenService.createSpecimens(specimenData);
         if (specimenList!=null) {
            return ResponseEntity.ok(specimenList);
        } else {
            throw APIException.notFound("TestType Number {0} not found.", "");
        }
     
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
    
    @GetMapping("/specimen/{id}")
    public ResponseEntity<?> fetchSpecimenById(@PathVariable("id") final Long id) {
        SpecimenData specimens = specimenService.fetchSpecimenById(id);
        if (specimens!=null) {
            return ResponseEntity.ok(specimens);
        } else {
            throw APIException.notFound("specimen Number {0} not found.", id);
        }
    }

    @GetMapping("/specimen")
    public ResponseEntity<List<SpecimenData>> fetchAllSpecimens(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {

        Page<SpecimenData> page = specimenService.fetchAllSpecimens(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @DeleteMapping("/specimen/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        specimenService.deleteById(id);
        return ResponseEntity.ok("200");
    }
}

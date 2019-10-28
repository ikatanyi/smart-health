/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.infrastructure.exception.APIException;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/consultation")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to Doctor Requests/Orders maintenance")
public class DoctorRequestController {
    @Autowired
    DoctorRequestService requestService;
    
    
    @PostMapping("/doctorRequest")
    public @ResponseBody
    ResponseEntity<?> createRequest(@RequestBody @Valid final List<DoctorRequestData> docRequestData) {
        List<DoctorRequestData> requestList = requestService.createRequest(docRequestData);
         if (requestList!=null) {
            return ResponseEntity.ok(requestList);
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
    
    @GetMapping("/doctorRequest/{id}")
    public ResponseEntity<?> fetchRequestById(@PathVariable("id") final Long id) {
        Optional<DoctorRequestData> specimens = requestService.getDocRequestById(id);
        if (specimens!=null) {
            return ResponseEntity.ok(specimens);
        } else {
            throw APIException.notFound("Request Number {0} not found.", id);
        }
    }

    @GetMapping("/docRequest")
    public ResponseEntity<List<DoctorRequestData>> fetchAllSpecimens(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {
        String visitNumber = queryParams.getFirst("visitNumber");
        String status = queryParams.getFirst("status");
        String requestType = queryParams.getFirst("requestType");
        String from = queryParams.getFirst("from");
        String to = queryParams.getFirst("to");
        List<DoctorRequestData> page = requestService.findAll(visitNumber, status, requestType, from, to, pageable);
        HttpHeaders headers = null;//PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page.);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }
    
    @DeleteMapping("/docRequest/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        return requestService.deleteById(id);
    }
    
}

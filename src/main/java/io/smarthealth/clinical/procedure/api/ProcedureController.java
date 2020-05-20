/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.api;

import io.smarthealth.clinical.procedure.data.ProcedureData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/v1")
@Api(value = "Procedure-Controller", description = "Operations pertaining to Procedure maintenance")
public class ProcedureController {

    @Autowired
    ProcedureService radiologyService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/procedure")
    @PreAuthorize("hasAuthority('create_procedure')")
    public @ResponseBody
    ResponseEntity<?> createTests(@RequestBody @Valid final List<ProcedureData> procedureData) {
        List<ProcedureData> radiologyTestList = radiologyService.createProcedureTest(procedureData)
                .stream()
                .map((radiology)->{
                 ProcedureData testdata =  ProcedureData.map(radiology);
            return testdata;
            }).collect(Collectors.toList());
        Pager<List<ProcedureData>> pagers = new Pager();        
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(radiologyTestList);
        PageDetails details = new PageDetails();
        details.setReportName("Procedure Tests");
        pagers.setPageDetails(details);        
        return ResponseEntity.status(HttpStatus.OK).body(pagers);      
    }

    @GetMapping("/procedure/{id}")
    @PreAuthorize("hasAuthority('view_procedure')")
    public ResponseEntity<?> fetchProcedureTest(@PathVariable("id") final Long id) {
        ProcedureData radiologyTests = ProcedureData.map(radiologyService.getById(id));
        if (radiologyTests != null) {
            return ResponseEntity.ok(radiologyTests);
        } else {
            throw APIException.notFound("radiology Test Number {0} not found.", id);
        }
    }

    @GetMapping("/procedure")
    @PreAuthorize("hasAuthority('view_procedure')")
    public ResponseEntity<?> fetchAllProcedure(
       @RequestParam(value = "page", required = false) Integer page1,
       @RequestParam(value = "pageSize", required = false) Integer size
    ) {

        Pageable pageable = PaginationUtil.createPage(page1, size);
        List<ProcedureData> testData = radiologyService.findAll(pageable)
                .stream()
                .map((radiology)->{
                 ProcedureData testdata =  ProcedureData.map(radiology);
            return testdata;
            }).collect(Collectors.toList());
        Pager page = new Pager();
        page.setCode("200");
        page.setContent(testData);
        page.setMessage("Procedure Tests fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Procedure Tests fetched");
//        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
        page.setPageDetails(details);
        return ResponseEntity.ok(page);
    }
}

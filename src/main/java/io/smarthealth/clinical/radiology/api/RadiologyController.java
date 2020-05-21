/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.data.ServiceTemplateData;
import io.smarthealth.clinical.radiology.service.RadiologyConfigService;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.service.UploadService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
@RequestMapping("/api")
@Api(value = "Radiology-Controller", description = " Radiology Setup")
public class RadiologyController {

    @Autowired
    RadiologyService radiologyService;

    @Autowired
    RadiologyConfigService radiologyConfigService;

    @Autowired
    UploadService uploadService;

//    @PostMapping("/radiology-template")
//    public @ResponseBody
//    ResponseEntity<?> createServiceTemplate(@RequestBody @Valid final List<ServiceTemplateData> serviceTemplateData) {
//        List<ServiceTemplateData> serviceTemplateDataArr = radiologyConfigService.createServiceTemplate(serviceTemplateData)
//                .stream()
//                .map((template)->template.toData()
//            ).collect(Collectors.toList());
//        
//        Pager<List<ServiceTemplateData>> pagers = new Pager();        
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(serviceTemplateDataArr);
//        PageDetails details = new PageDetails();
//        details.setReportName("Service Templates");
//        pagers.setPageDetails(details);        
//        return ResponseEntity.status(HttpStatus.OK).body(pagers);      
//    }
    @PostMapping("/radiology-template")
    @PreAuthorize("hasAuthority('create_radiology')")
    public @ResponseBody
    ResponseEntity<?> uploadTemplate(@RequestBody @Valid final ServiceTemplateData serviceTemplateData) {
        ServiceTemplateData savedTemplateData = radiologyConfigService.saveTemplate(serviceTemplateData).toData();
        Pager<ServiceTemplateData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(savedTemplateData);
        PageDetails details = new PageDetails();
        details.setReportName("Service Templates");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PostMapping("/radiology-template/batch")
    @PreAuthorize("hasAuthority('create_radiology')")
    public @ResponseBody
    ResponseEntity<?> batchUploadTemplates(@RequestBody @Valid final List<ServiceTemplateData> serviceTemplateData) {
        List<ServiceTemplateData> serviceTemplateDataArr = radiologyConfigService.batchTemplateUpload(serviceTemplateData)
                .stream()
                .map((template) -> {
                    ServiceTemplateData templateData = template.toData();
                    return templateData;
                }).collect(Collectors.toList());

        Pager<List<ServiceTemplateData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(serviceTemplateDataArr);
        PageDetails details = new PageDetails();
        details.setReportName("Service Templates");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/radiology-template/{id}")
    @PreAuthorize("hasAuthority('view_radiology')")
    public ResponseEntity<?> fetchServiceTemplate(@PathVariable("id") final Long id) {
        ServiceTemplateData serviceTemplates = radiologyConfigService.getServiceTemplateByIdWithFailDetection(id).toData();
        if (serviceTemplates != null) {
            return ResponseEntity.ok(serviceTemplates);
        } else {
            throw APIException.notFound("radiology Test Number {0} not found.", id);
        }
    }

    @GetMapping("/radiology-template")
    @PreAuthorize("hasAuthority('view_radiology')")
    public ResponseEntity<?> fetchAllServiceTemplates(
            @RequestParam(value = "page", required = false) Integer page1,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {

        Pageable pageable = PaginationUtil.createPage(page1, size);
        List<ServiceTemplateData> testData = radiologyConfigService.findAllTemplates(pageable)
                .stream()
                .map((template) -> template.toData()
                ).collect(Collectors.toList());

        Pager page = new Pager();
        page.setCode("200");
        page.setContent(testData);
        page.setMessage("Service Templates fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Service Template fetched");
//        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
        page.setPageDetails(details);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/radiology-tests")
    @PreAuthorize("hasAuthority('create_radiology')")
    public @ResponseBody
    ResponseEntity<?> createTests(@RequestBody @Valid final List<RadiologyTestData> radiologyTestData) {
        List<RadiologyTestData> radiologyTestList = radiologyConfigService.createRadiologyTest(radiologyTestData)
                .stream()
                .map((radiology) -> {
                    RadiologyTestData testdata = radiology.toData();
                    return testdata;
                }).collect(Collectors.toList());
        Pager<List<RadiologyTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(radiologyTestList);
        PageDetails details = new PageDetails();
        details.setReportName("Radiology Tests");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/radiology-tests/{id}")
    @PreAuthorize("hasAuthority('view_radiology')")
    public ResponseEntity<?> fetchRadiologyTest(@PathVariable("id") final Long id) {
        RadiologyTestData radiologyTests = radiologyConfigService.getById(id).toData();
        if (radiologyTests != null) {
            return ResponseEntity.ok(radiologyTests);
        } else {
            throw APIException.notFound("radiology Test Number {0} not found.", id);
        }
    }

    @GetMapping("/radiology-tests")
    @PreAuthorize("hasAuthority('view_radiology')")
    public ResponseEntity<?> fetchAllRadiology(
            @RequestParam(value = "page", required = false) Integer page1,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {

        Pageable pageable = PaginationUtil.createPage(page1, size);
        List<RadiologyTestData> testData = radiologyConfigService.findAll(pageable)
                .stream()
                .map((radiology) -> {
                    RadiologyTestData testdata = radiology.toData();
                    return testdata;
                }).collect(Collectors.toList());
        Pager page = new Pager();
        page.setCode("200");
        page.setContent(testData);
        page.setMessage("Radiology Tests fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Radiology Tests fetched");
        page.setPageDetails(details);
        return ResponseEntity.ok(page);
    }
}

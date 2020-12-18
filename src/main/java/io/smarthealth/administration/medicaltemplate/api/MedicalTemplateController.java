/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.medicaltemplate.api;

import io.smarthealth.administration.medicaltemplate.data.MedicalTemplateData;
import io.smarthealth.administration.medicaltemplate.service.MedicalTemplateService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Medical-Template-Controller", description = "Medical Templates Setup")
@RequiredArgsConstructor
public class MedicalTemplateController {

    private final MedicalTemplateService medicalTemplateService;
    private final AuditTrailService auditTrailService; 

    @PostMapping("/medical-template")
    @PreAuthorize("hasAuthority('Administration_Module')")
    public @ResponseBody
    ResponseEntity<?> saveTemplate(@RequestBody @Valid final MedicalTemplateData medicalTemplateData) {
        MedicalTemplateData savedTemplateData = medicalTemplateService.saveMedicalTemplate(medicalTemplateData).toData();
        auditTrailService.saveAuditTrail("Administration", "Created Medical template  "+medicalTemplateData.getTemplateName());
        Pager<MedicalTemplateData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(savedTemplateData);
        PageDetails details = new PageDetails();
        details.setReportName("Medical Template");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    
    @GetMapping("/medical-template/{templateName}")
    public @ResponseBody
    ResponseEntity<?> getTemplate(
            @PathVariable("templateName") final String templateName
    ) {
        MedicalTemplateData savedTemplateData = medicalTemplateService.fetchMedicalTemplateByName(templateName).toData();
        auditTrailService.saveAuditTrail("Administration", "viewed Medical template  "+savedTemplateData.getTemplateName());
        Pager<MedicalTemplateData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(savedTemplateData);
        PageDetails details = new PageDetails();
        details.setReportName("Medical Template");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
}

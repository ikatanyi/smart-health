/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.api;

import io.smarthealth.administration.medicaltemplate.data.MedicalTemplateData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.clinical.wardprocedure.data.ConsentFormData;
import io.smarthealth.clinical.wardprocedure.data.DoctorNotesData;
import io.smarthealth.clinical.wardprocedure.service.ConsentService;
import io.smarthealth.infrastructure.utility.ListData;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Consent forms generation", description = "Medical consent form")
@RequiredArgsConstructor
public class ConsentFormController {

    private final ConsentService consentService;
    

    @PostMapping("/consent")
    public @ResponseBody
    ResponseEntity<?> saveConsentForm(@RequestBody @Valid final ConsentFormData consentFormData) {
        ConsentFormData savedTemplateData = consentService.saveConsentForm(consentFormData).toData();
        Pager<ConsentFormData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(savedTemplateData);
        PageDetails details = new PageDetails();
        details.setReportName("Consent Form");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/consent")
    public @ResponseBody
    ResponseEntity<?> fetchConsentForm(
            @RequestParam(value = "visitNumber", required = true) final String visitNumber,
            @RequestParam(value = "consentType", required = false) final String consentType
    ) {

        List<ConsentFormData> consentFormDatas = consentService.fetchConsentFormsByVisit(visitNumber).stream().map(t -> t.toData()).collect(Collectors.toList());
        if (consentType != null) {
            consentService.fetchConsentFormsByVisitAndType(visitNumber, consentType).stream().map(t -> t.toData()).collect(Collectors.toList());
        }
        ListData<ConsentFormData> listData = new ListData();
        listData.setCode("200");
        listData.setMessage("Success");
        listData.setContent(consentFormDatas);
        return ResponseEntity.status(HttpStatus.OK).body(listData);
    }

}

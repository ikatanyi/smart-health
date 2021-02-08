/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.integration.data.ClaimFileData;
import io.smarthealth.integration.metadata.CardData.AdmissionInformation;
import io.smarthealth.integration.metadata.CardData.CardData;
import io.smarthealth.integration.metadata.CardData.Root;
import io.smarthealth.integration.service.IntegrationService;
import io.swagger.annotations.Api;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/integration")
@Api(value = "Integration Controller", description = "Operations pertaining to Integration")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @PostMapping("/smart/claim/{memberNumber}")
    public @ResponseBody
    ResponseEntity<?> createClaimFile(@PathVariable("memberNumber") final String memberNumber, @RequestBody @Valid final ClaimFileData claimFileData) throws JsonProcessingException {        
//        ExchangeFile result = integrationService.createClaim(claimFileData);        
//        Pager<ExchangeFileData> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Smart Claim made successfully");
//        pagers.setContent(ExchangeFileData.map(result));

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @GetMapping("/smart/member-profile")
    public @ResponseBody
    ResponseEntity<?> fetchClaim(
        @RequestParam(value = "memberNumber", required = true) final String memberNumber
    ) throws JsonProcessingException {

        CardData result = integrationService.findByPatientId(memberNumber);
        Pager pager = new Pager();
        pager.setCode("200");
        pager.setContent(result);
        pager.setMessage("Member Profile fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Member Profile");
        pager.setPageDetails(details);
        return ResponseEntity.ok(pager);
    }
}

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
import io.smarthealth.integration.data.ExchangeFileData;
import io.smarthealth.integration.data.SmartFileData;
import io.smarthealth.integration.domain.ExchangeFile;
import io.smarthealth.integration.service.IntegrationService;
import io.swagger.annotations.Api;
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
public class smartController {

    @Autowired
    IntegrationService integrationService;

    @PostMapping("/smart/claim/{memberNumber}")
    public @ResponseBody
    ResponseEntity<?> createClaimFile(@PathVariable("memberNumber") final String memberNumber, @RequestBody @Valid final ClaimFileData claimFileData) throws JsonProcessingException {        
        ExchangeFile result = integrationService.createclaimFile(memberNumber, claimFileData);        
        Pager<ExchangeFileData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Smart Claim made successfully");
        pagers.setContent(ExchangeFileData.map(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/smart/smartFile")
    public @ResponseBody
    ResponseEntity<?> fetchAllAppointments(
        @RequestParam(value = "memberNumber", required = true) final String memberNumber,
        @RequestParam(value = "progressFlag", required = false) final Long progressFlag
    ) throws JsonProcessingException {
        
        SmartFileData result = integrationService.fetchSmartFile(memberNumber, progressFlag);
        
        Pager pager = new Pager();
        pager.setCode("200");
        pager.setContent(result);
        pager.setMessage("Smart Dump file fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Smart Dump File");
        pager.setPageDetails(details);
        return ResponseEntity.ok(pager);
    }
}

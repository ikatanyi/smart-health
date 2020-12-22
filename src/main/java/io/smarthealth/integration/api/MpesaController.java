/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.api;

import io.smarthealth.integration.service.MpesaService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/v1/smartpayments")
public class MpesaController {
 
    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmation(@RequestBody String response) {
        log.info("Acknowledging Safaricom Response...");
        log.info(response);

        return ResponseEntity.ok("Successful");
    }

    @PostMapping("/initiate-stk-push/{phoneNo}" )
    public @ResponseBody
    ResponseEntity<?> initiateSTKPush(@PathVariable("phoneNo") final String phoneNo) {
        return ResponseEntity.ok(mpesaService.initiateStkPush(phoneNo, BigDecimal.valueOf(10)));
    }

}

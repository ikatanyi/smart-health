/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.api;

import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegrationRepository;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import io.smarthealth.infrastructure.common.IntegrationStatus;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.integration.data.MobileMoneyResponseData;
import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.service.MobileMoneyProcessingService;
import io.smarthealth.integration.service.MpesaService;
import io.swagger.annotations.Api;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Kelsas
 */
@Api
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MpesaController {

    private final MpesaService mpesaService;
    private final MobileMoneyProcessingService moneyProcessingService;

    private final MobileMoneyIntegrationRepository mobileMoneyIntegrationRepository;


    @PostMapping("/smartpayments/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmationFromProvidersSide(@RequestBody String response) {
        log.info("Acknowledging Safaricom Response Remotely...");
        log.info(response);
        MobileMoneyResponse savedResponse = mpesaService.saveMobileMoneyResponse(response);

        return ResponseEntity.ok("Successful");
    }

    @PostMapping("/confirm/{phoneNumber}/provider/{providerId}")
    @ResponseBody
    public ResponseEntity<?> confirmationLocalDB(
            @PathVariable("phoneNumber") String phoneNumber,
            @PathVariable("providerId") final Long providerId
    ) {
        log.info("Acknowledging Safaricom Response locally...");

        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.replaceFirst("0", "254");
        }
//        phoneNumber = phoneNumber.replaceAll(String.valueOf('+'), "");


        //validate if provider integration is active
        MobileMoneyIntegration moneyIntegration =
                mobileMoneyIntegrationRepository.findById(providerId).orElseThrow(() -> APIException.notFound("No " +
                        "provider identified by id {0} found ", providerId));
        if (moneyIntegration.getStatus().equals(IntegrationStatus.InActive)) {
            throw APIException.notFound("No integration settings for provider {0} .", providerId);
        }
        //respond the response with unique status
        MobileMoneyResponse moneyResponse = moneyProcessingService.findRecentByPhoneNumberOrNull(phoneNumber);
        Pager<MobileMoneyResponseData> pagers = new Pager();
        if (moneyResponse == null) {
            pagers.setCode("404");
            pagers.setMessage("Response not found. Please try again!");
            pagers.setContent(null);
        }
        if (moneyResponse != null) {
            pagers.setCode("200");
            pagers.setMessage("Response found.");
            pagers.setContent(MobileMoneyResponseData.map(moneyResponse));
        }
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PostMapping("/initiate-stk-push/{phoneNo}")
    public @ResponseBody
    ResponseEntity<?> initiateSTKPush(@PathVariable("phoneNo") final String phoneNo) {
        return ResponseEntity.ok(mpesaService.initiateStkPush(phoneNo, BigDecimal.valueOf(10)));
    }

}

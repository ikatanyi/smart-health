/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.service;

import io.smarthealth.integration.data.Mpesa;
import io.smarthealth.integration.data.MpesaRequest;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class MpesaService {

    @Value("${smarthealth.mpesa.base-uri}")
    private String baseUri;
    private final OAuth2RestTemplate  restTemplate;
    private final Mpesa mpesa;

    public MpesaService(OAuth2RestTemplate restTemplate, Mpesa mpesa) {
        this.restTemplate = restTemplate;
        this.mpesa = mpesa;
    }

    public String initiateStkPush(String phoneNumber, BigDecimal amount) { 
        String callbackUrl = "https://459f5af351e2.ngrok.io/api/v1/smartpayments/confirm";

        MpesaRequest request = new MpesaRequest()
                .BusinessShortCode(mpesa.getShortCode())
                .Password(mpesa.getPassword())
                .Timestamp(mpesa.getTimestamp())
                .TransactionType("CustomerBuyGoodsOnline")
                .Amount(amount.toPlainString())
                .PartyA(phoneNumber)
                .PartyB(mpesa.getShortCode())
                .PhoneNumber(phoneNumber)
                .CallBackURL(callbackUrl)
                .AccountReference("PT-00800-20")
                .TransactionDesc("Medical Bill");

        log.info("Response: {}", restTemplate.postForEntity(baseUri+"/stkpush/v1/processrequest", request, String.class));

        return request.toString();
    }
}

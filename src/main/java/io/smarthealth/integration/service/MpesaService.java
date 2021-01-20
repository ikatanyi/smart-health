package io.smarthealth.integration.service;

import io.smarthealth.integration.config.MpesaProperties;
import io.smarthealth.integration.data.MpesaRequest;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class MpesaService {

    private final OAuth2RestTemplate restTemplate;
    private final MpesaProperties mpesaConfiguration;

    public MpesaService(OAuth2RestTemplate restTemplate, MpesaProperties mpesaConfiguration) {
        this.restTemplate = restTemplate;
        this.mpesaConfiguration = mpesaConfiguration;
    }

    @Transactional
    public String initiateStkPush(String phoneNumber, BigDecimal amount) {

        MpesaRequest request = new MpesaRequest();

        request.setBusinessShortCode(mpesaConfiguration.getShortCode());
        request.setPassword(mpesaConfiguration.getPassword());
        request.setTimestamp(mpesaConfiguration.getTimestamp());
        request.setTransactionType(mpesaConfiguration.getTransactionType());
        request.setAmount(amount.toPlainString());
        request.setPartyA(phoneNumber);
        request.setPartyB(mpesaConfiguration.getShortCode());
        request.setPhoneNumber(phoneNumber);
        request.setCallBackURL(mpesaConfiguration.getCallbackUrl());
        request.setAccountReference("PT-00800-20");
        request.setTransactionDesc("Medical Bill");

        String url = mpesaConfiguration.getBaseUri() + "/stkpush/v1/processrequest";

        log.info("Response: {}", restTemplate.postForEntity(url, request, String.class));

        return request.toString();
    }
}

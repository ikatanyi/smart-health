package io.smarthealth.integration.service;

import io.smarthealth.integration.config.MpesaConfiguration;
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
    private final MpesaConfiguration mpesaConfiguration;

    public MpesaService(OAuth2RestTemplate restTemplate, MpesaConfiguration mpesaConfiguration) {
        this.restTemplate = restTemplate;
        this.mpesaConfiguration = mpesaConfiguration;
    }

    @Transactional
    public String initiateStkPush(String phoneNumber, BigDecimal amount) {
        MpesaRequest request = new MpesaRequest()
                .BusinessShortCode(mpesaConfiguration.getShortCode())
                .Password(mpesaConfiguration.getPassword())
                .Timestamp(mpesaConfiguration.getTimestamp())
                .TransactionType(mpesaConfiguration.getTransactionType())
                .Amount(amount.toPlainString())
                .PartyA(phoneNumber)
                .PartyB(mpesaConfiguration.getShortCode())
                .PhoneNumber(phoneNumber)
                .CallBackURL(mpesaConfiguration.getCallbackUrl())
                .AccountReference("PT-00800-20")
                .TransactionDesc("Medical Bill");

        String url = mpesaConfiguration.getBaseUri() + "/stkpush/v1/processrequest";

        log.info("Response: {}", restTemplate.postForEntity(url, request, String.class));

        return request.toString();
    }
}

package io.smarthealth.integration.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.integration.config.MpesaProperties;
import io.smarthealth.integration.data.MpesaRequest;

import java.math.BigDecimal;

import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.domain.MobileMoneyResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

/**
 * @author Kelsas
 */
@Service
@Slf4j
public class MpesaService {

    private final OAuth2RestTemplate restTemplate;
    private final MpesaProperties mpesaConfiguration;
    private final MobileMoneyResponseRepository moneyResponseRepository;

    public MpesaService(OAuth2RestTemplate restTemplate, MpesaProperties mpesaConfiguration, MobileMoneyResponseRepository moneyResponseRepository) {
        this.restTemplate = restTemplate;
        this.mpesaConfiguration = mpesaConfiguration;
        this.moneyResponseRepository = moneyResponseRepository;
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

    @Transactional
    public MobileMoneyResponse saveMobileMoneyResponse(String response) {
        String transactionType;
        String transID;
        String transTime;
        String transAmount;
        String businessShortCode;
        String billRefNumber;
        String invoiceNumber;
        String orgAccountBalance;
        String phoneNo;
        String firstName;
        String middleName;
        String lastName;
        MobileMoneyResponse responseObj = new MobileMoneyResponse();
        try {
            JSONObject j = new JSONObject(response);
            transactionType = j.getString("TransactionType");
            transID = j.getString("TransID");
            transTime = j.getString("TransTime");
            transAmount = j.getString("TransAmount");
            businessShortCode = j.getString("BusinessShortCode");
            billRefNumber = j.getString("BillRefNumber");
            invoiceNumber = j.getString("InvoiceNumber");
            orgAccountBalance = j.getString("OrgAccountBalance");
            phoneNo = j.getString("MSISDN");
            firstName = j.getString("FirstName");
            middleName = j.getString("MiddleName");
            lastName = j.getString("LastName");

            responseObj.setBillRefNumber(billRefNumber);
            responseObj.setBusinessShortCode(businessShortCode);
            responseObj.setFirstName(firstName);
            responseObj.setInvoiceNumber(invoiceNumber);
            responseObj.setMiddleName(middleName);
            responseObj.setOrgAccountBalance(orgAccountBalance);
            responseObj.setPhoneNo(phoneNo);
            responseObj.setTransAmount(transAmount);
            responseObj.setTransID(transID);
            responseObj.setTransTime(transTime);
            responseObj.setTransactionType(transactionType);
            responseObj.setPatientBillEffected(Boolean.FALSE);
            
            return moneyResponseRepository.save(responseObj);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occurred while processing request");
        }
    }
}

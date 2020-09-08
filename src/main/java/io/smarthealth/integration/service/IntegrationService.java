/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.service;

import io.smarthealth.ApplicationProperties;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.integration.data.ClaimFileData;
import io.smarthealth.integration.data.ExchangeFileData;
import io.smarthealth.integration.domain.ExchangeFileRepository;
import io.smarthealth.integration.domain.ExchangeLocationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class IntegrationService {

    @Autowired
    RestTemplate restTemplate;

    private final ApplicationProperties properties;

    ExchangeLocationsRepository exchangeLocationRepository;
    ExchangeFileRepository exchangeFileRepository;
    FacilityService facilityService;

    
    /**
     * Consuming a service by postForObject method, this method is exposed as a
     * get operation if user doesn 't post a request object we will create a new
     * request and post it to the URL /service endpoint
     *
     */
    public ExchangeFileData getClaimStatus(String location) {
        return restTemplate.postForObject("ws://"+properties.getIntegServer()+"/"+location, "", ExchangeFileData.class);
    }

    /**
     * Consuming a service by postForEntity method, this method is exposed as a
     * post operation if user post a request object(JSON) it will be
     * automatically mapped to Request parameter.
     */
    
    public ExchangeFileData createClaim(Invoice invoice) {
        ClaimFileData data = new ClaimFileData();
        ExchangeFileData response = restTemplate.postForObject(properties.getIntegServer(), data.toData(invoice),  ExchangeFileData.class);
        return response;
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

}

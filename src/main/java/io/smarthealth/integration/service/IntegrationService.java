/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.smarthealth.integration.domain.ExchangeFile;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.integration.data.ClaimFileData;
import io.smarthealth.integration.data.SmartFileData;
import io.smarthealth.integration.domain.ExchangeFileRepository;
import io.smarthealth.integration.domain.ExchangeLocationsRepository;
import io.smarthealth.integration.metadata.claim;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class IntegrationService {

    ExchangeLocationsRepository exchangeLocationRepository;
    ExchangeFileRepository exchangeFileRepository;
    FacilityService facilityService;

    public IntegrationService(ExchangeLocationsRepository exchangeLocationRepository, ExchangeFileRepository exchangeFileRepository, FacilityService facilityService) {
        this.exchangeLocationRepository = exchangeLocationRepository;
        this.exchangeFileRepository = exchangeFileRepository;
        this.facilityService = facilityService;
    }

    public SmartFileData fetchSmartFile(String memberNr, Long progressFlag) throws JsonProcessingException {
        SmartFileData value = null;
        SmartFileData result = new SmartFileData();
        ExchangeFile exFile = fetchExchangeFileByMemberNrOrThrow(memberNr);
        XmlMapper xmlMapper = new XmlMapper();
        if (exFile.getResultFile() != null) {
            value = xmlMapper.readValue(exFile.getResultFile(), SmartFileData.class);
        }

        return value;
    }

    public ExchangeFile createclaimFile(String memberNr, ClaimFileData fileData) throws JsonProcessingException {
        SmartFileData value = null;

        SmartFileData result = fetchSmartFile(memberNr, 1L);
        Facility facility = facilityService.loggedFacility();
        claim claimFile = ClaimFileData.map(fileData, result);

        claimFile.getClaimHeader().getProvider().setGroupPracticeName(facility.getFacilityName());
        claimFile.getClaimHeader().getProvider().setGroupPracticeNumber(facility.getRegistrationNumber());
        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(claimFile);
        ExchangeFile exFile = fetchExchangeFileByMemberNrOrThrow(memberNr);        
        exFile.setResultFile(xml);
        return exchangeFileRepository.save(exFile);
    }

    public ExchangeFile fetchExchangeFileByMemberNrOrThrow(final String memberNumber) {
        return exchangeFileRepository.findByMemberNrAndProgressFlag(memberNumber, 1L).orElseThrow(() -> APIException.notFound("ExchangeFile identified by number {0} was not found ", memberNumber));
    }
}

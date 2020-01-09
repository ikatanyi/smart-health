/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.smart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.smarthealth.accounting.smart.data.SmartFileData;
import io.smarthealth.accounting.smart.domain.ExchangeFile;
import io.smarthealth.accounting.smart.domain.ExchangeFileRepository;
import io.smarthealth.accounting.smart.domain.ExchangeLocationsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class SmartService {
    ExchangeLocationsRepository exchangeLocationRepository;
    ExchangeFileRepository exchangeFileRepository;

    public SmartService(ExchangeLocationsRepository exchangeLocationRepository, ExchangeFileRepository exchangeFileRepository) {
        this.exchangeLocationRepository = exchangeLocationRepository;
        this.exchangeFileRepository = exchangeFileRepository;
    }
    
    public SmartFileData fetchSmartDetails(String memberNr, Long progressFlag) throws JsonProcessingException{
        SmartFileData value=null;
        SmartFileData result = new SmartFileData();
        Optional<ExchangeFile> exFile = exchangeFileRepository.findByMemberNrAndProgressFlag(memberNr, progressFlag);
        if(!exFile.isPresent())
             APIException.notFound("SL Dump File  not found with memberNr", memberNr);
        else{
            XmlMapper xmlMapper = new XmlMapper();
            ExchangeFile file = exFile.get();
            if(file.getResultFile()!=null)
               value  = xmlMapper.readValue(file.getResultFile(), SmartFileData.class);
        }
            
        return value;
    }
}

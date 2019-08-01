/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.service;

import io.smarthealth.administration.data.CodesData;
import io.smarthealth.administration.domain.CodeType;
import io.smarthealth.administration.domain.CodeTypeRepository;
import io.smarthealth.administration.domain.Codes;
import io.smarthealth.administration.domain.CodesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class CodesService {

    @Autowired
    CodesRepository codesRepository;

    @Autowired
    CodeTypeRepository codeTypeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public Codes createCode(CodesData codesData) {
        try {
            Codes codes = modelMapper.map(codesData, Codes.class);
            //fetch Code type by ctKey
            CodeType codeType = codeTypeRepository.findByCtKey(codesData.getCodeTypeKeyId()).orElseThrow(() -> APIException.notFound("Code type key identified by key {0} not found", codesData.getCodeTypeKeyId()));
            codes.setCodeType(codeType);
            codesRepository.save(codes);
            return codes;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error creating code", e.getMessage());
        }
    }

    public Page<Codes> fetchAllCodes(final Pageable pageable) {
        return codesRepository.findAll(pageable);
    }

    public Page<Codes> fecthAlCodesByCodeType(final CodeType codeType, final Pageable pageable) {
        return codesRepository.findByCodeType(codeType, pageable);
    }
}

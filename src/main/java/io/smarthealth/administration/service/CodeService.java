/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.service;

import io.smarthealth.administration.data.CodeTypeData;
import io.smarthealth.administration.domain.CodeType;
import io.smarthealth.administration.domain.CodeTypeRepository;
import io.smarthealth.infrastructure.exception.APIException;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class CodeService {

    @Autowired
    CodeTypeRepository codeTypeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public CodeType createNewCodeType(CodeType codeType) {
        try {
            return codeTypeRepository.save(codeType);

        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error creating code type ", e.getMessage());
        }
    }

    public CodeType fetchCodeTypeByCtKey(final String ctKeyCode) {
        try {
            return codeTypeRepository.findByCtKey(ctKeyCode).orElseThrow(() -> APIException.notFound("Could not find code type identofied by ct_key {0}", ctKeyCode));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching code type identified by " + ctKeyCode, e.getMessage());
        }
    }

    public Page<CodeType> fetchAllCodeTypes(final Pageable pageable) {
        try {
            return codeTypeRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error while fetching all type codes ", e.getMessage());
        }
    }

    public CodeType updateCodeType(final String codeId, CodeTypeData codeTypeData) {
        try {
            //find codeType by codeId
            CodeType codeType = codeTypeRepository.findById(codeId).orElseThrow(() -> APIException.notFound("No code type identified by {0} found ", codeId));
            modelMapper.map(codeTypeData, codeType);
            codeType.setCtCategory(codeTypeData.getCtCategory());
            codeTypeRepository.save(codeType);
            return codeType;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error while updating code type", e.getMessage());
        }
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.codes.service;

import io.smarthealth.infrastructure.codes.domain.CodeRepository;
import io.smarthealth.infrastructure.codes.domain.CodeValueRepository;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class CodesService {

    private final CodeRepository codeRepository;
    private final CodeValueRepository codeValueRepository;

    public CodesService(CodeRepository codeRepository, CodeValueRepository codeValueRepository) {
        this.codeRepository = codeRepository;
        this.codeValueRepository = codeValueRepository;
    }
    
}

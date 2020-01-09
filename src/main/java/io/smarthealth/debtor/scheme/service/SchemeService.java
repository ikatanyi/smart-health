/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.service;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.SchemeRepository;
import io.smarthealth.debtor.scheme.domain.InsuranceSchemeRepository;
import io.smarthealth.infrastructure.exception.APIException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class SchemeService {

    @Autowired
    SchemeRepository schemeRepository;

    @Transactional
    public Scheme createScheme(Scheme s) {
        return schemeRepository.save(s);
    }

    public Page<Scheme> fetchSchemes(Pageable p) {
        return schemeRepository.findAll(p);
    }

    public Scheme fetchSchemeById(Long id) {
        return schemeRepository.findById(id).orElseThrow(() -> APIException.notFound("Scheme identified by id {0} not available ", id));
    }
}

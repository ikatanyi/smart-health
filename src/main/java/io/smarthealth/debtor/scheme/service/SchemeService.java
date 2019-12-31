/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.service;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.SchemeRepository;
import io.smarthealth.debtor.scheme.domain.InsuranceSchemeRepository;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurationsRepository;
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

    @Autowired
    SchemeConfigurationsRepository configurationsRepository;

    @Transactional
    public Scheme createScheme(Scheme s) {
        return schemeRepository.save(s);
    }

    @Transactional
    public SchemeConfigurations updateSchemeConfigurations(SchemeConfigurations configurations) {
        return configurationsRepository.save(configurations);
    }

    public Page<Scheme> fetchSchemes(Pageable p) {
        return schemeRepository.findAll(p);
    }

    public Scheme fetchSchemeById(Long id) {
        return schemeRepository.findById(id).orElseThrow(() -> APIException.notFound("Scheme identified by id {0} not available ", id));
    }

    public SchemeConfigurations fetchSchemeConfigById(Long id) {
        return configurationsRepository.findById(id).orElseThrow(() -> APIException.notFound("Scheme configuration identified by id {0} not available ", id));
    }

    public SchemeConfigurations fetchSchemeConfigBySchemeWithNotAvailableDetection(Scheme scheme) {
        return configurationsRepository.findByScheme(scheme).orElseThrow(() -> APIException.notFound("Scheme configuration identified by scheme no {0} not available ", scheme.getSchemeName()));
    }

    public boolean SchemeConfigBySchemeExists(Scheme scheme) {
        return configurationsRepository.findByScheme(scheme).isPresent();
    }
}

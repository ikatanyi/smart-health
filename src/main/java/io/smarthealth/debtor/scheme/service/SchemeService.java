/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.service;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.SchemeRepository;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurationsRepository;
import io.smarthealth.debtor.scheme.domain.specification.SchemeSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<Scheme> fetchSchemes(final String term, Pageable p) {
        Specification<Scheme> spec = SchemeSpecification.createSchemeSpecification(null, term);
        return schemeRepository.findAll(spec, p);
    }

    public Page<Scheme> fetchSchemesByPayer(final Payer payer, final String term, Pageable page) {
        Specification<Scheme> spec = SchemeSpecification.createSchemeSpecification(payer, term);
        return schemeRepository.findAll(spec, page);
    }

    public Optional<SchemeConfigurations> fetchSchemeConfigByScheme(Scheme scheme) {
        return configurationsRepository.findByScheme(scheme);
    }

    public Optional<Scheme> fetchSchemeBySchemeName(String schemeName) {
        return schemeRepository.findBySchemeName(schemeName);
    }

    public Scheme fetchSchemeById(Long id) {
        return schemeRepository.findById(id).orElseThrow(() -> APIException.notFound("Scheme identified by id {0} not available ", id));
    }

    public Scheme fetchSchemeByCode(String code) {
        return schemeRepository.findBySchemeCode(code).orElseThrow(() -> APIException.notFound("Scheme identified by code {0} not available ", code));
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

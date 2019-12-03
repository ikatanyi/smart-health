/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.service;

import io.smarthealth.debtor.scheme.domain.InsuranceScheme;
import io.smarthealth.debtor.scheme.domain.InsuranceSchemeRepository;
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
    InsuranceSchemeRepository insuranceSchemeRepository;

    @Transactional
    public InsuranceScheme createScheme(InsuranceScheme s) {
        return insuranceSchemeRepository.save(s);
    }

    public Page<InsuranceScheme> fetchSchemes(Pageable p) {
        return insuranceSchemeRepository.findAll(p);
    }
}

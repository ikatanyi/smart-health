/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.Referrals;
import io.smarthealth.clinical.record.domain.ReferralsRepository;
import io.smarthealth.clinical.record.domain.specification.ReferralSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class ReferralsService {

    @Autowired
    ReferralsRepository referralsRepository;

    @Autowired
    VisitService visitService;

    @Transactional
    public Referrals createReferrals(final Referrals r) {
        return referralsRepository.save(r);
    }

    public Referrals fetchReferalByVisitOrThrowIfNotFound(final Visit visit) {
        return referralsRepository.findByVisit(visit).orElseThrow(() -> APIException.notFound("Referral details identifed by visit number {0} was not found", visit.getVisitNumber()));
    }

    public Page<Referrals> fetchReferrals(final String visitNumber, final String patientNumber, final Pageable pgbl) {
        Specification<Referrals> spec = ReferralSpecification.createSpecification(visitNumber, patientNumber);
        return referralsRepository.findAll(spec, pgbl);
    }
}

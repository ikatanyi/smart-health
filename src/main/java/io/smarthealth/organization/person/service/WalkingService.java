/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.data.specification.WalkingSpecification;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.domain.WalkingRepository;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class WalkingService {

    private final WalkingRepository walkingRepository;
    private final SequenceNumberService sequenceNumberService;

    @Transactional
    public WalkIn createWalking(final WalkIn walking) {
        walking.setWalkingIdentitificationNo(sequenceNumberService.next(1L, Sequences.WalkIn.name()));
        return walkingRepository.save(walking);
    }

    public Optional<WalkIn> fetchWalkingById(Long id) {
        return walkingRepository.findById(id);
    }

    public WalkIn fetchWalkingByIdWithNotFoundDetection(Long id) {
        return walkingRepository.findById(id).orElseThrow(() -> APIException.notFound("Walking identified by id {0} not found", id));
    }

    public Optional<WalkIn> fetchWalkingByWalkingNo(String walkingNo) {
        return walkingRepository.findByWalkingIdentitificationNo(walkingNo);
    }

    public WalkIn fetchWalkingByWalkingNoWithNotFoundDetection(String walkingNo) {
        return walkingRepository.findByWalkingIdentitificationNo(walkingNo).orElseThrow(() -> APIException.notFound("Walking identified by {0} not found", walkingNo));
    }

    public Page<WalkIn> fetchWalkingPatients(MultiValueMap<String, String> queryParams, final Pageable pageable) {
        Specification<WalkIn> spec = WalkingSpecification.createSpecification(queryParams.getFirst("searchValue"));
        return walkingRepository.findAll(spec, pageable);
    }

    public void removeWalking(final WalkIn w) {
        walkingRepository.delete(w);
    }

}

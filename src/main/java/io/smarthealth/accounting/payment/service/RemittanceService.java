/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.RemittanceRepository;
import io.smarthealth.accounting.payment.domain.specification.RemittanceSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RemittanceService {

    private final RemittanceRepository repository;

    public Remittance save(Remittance remittance) {
        return repository.save(remittance);
    }

    public Optional<Remittance> getRemittance(Long id) {
        return repository.findById(id);
    }

    public Remittance getRemittanceOrThrow(Long id) {
        return getRemittance(id)
                .orElseThrow(() -> APIException.notFound("Remittance with id {0} Not Found", id));
    }

    public Page<Remittance> getRemittances(Long payerId, String receipt, String remittanceNo, Boolean hasBalance, DateRange range, Pageable page) {
        Specification<Remittance> spec = RemittanceSpecification.createSpecification(payerId, receipt, remittanceNo, hasBalance, range);
        return repository.findAll(spec, page);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.RemittanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RemittanceService {

    private final RemittanceRepository repository;

    public Remittance save(Remittance remittance){
        return repository.save(remittance);
    }
    
}

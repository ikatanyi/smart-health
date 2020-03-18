/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.domain.VisitPaymentDetails;
import io.smarthealth.clinical.visit.domain.VisitPaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class VisitPaymentDetailsService {

    @Autowired
    VisitPaymentDetailsRepository visitPaymentDetailsRepository;

    public VisitPaymentDetails createVisitPaymentDetails(VisitPaymentDetails vpd) {
        return visitPaymentDetailsRepository.save(vpd);
    }
}

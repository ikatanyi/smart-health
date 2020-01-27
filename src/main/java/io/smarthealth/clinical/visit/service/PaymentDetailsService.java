/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.PaymentDetailsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author simz
 */
@Service
public class PaymentDetailsService {

    @Autowired
    PaymentDetailsRepository paymentDetailsRepository;

    @Transactional
    public PaymentDetails createPaymentDetails(PaymentDetails paymentDetails) {
        return paymentDetailsRepository.save(paymentDetails);
    }

    public PaymentDetails fetchPaymentDetailsByVisit(Long visitId) {
        return paymentDetailsRepository.findById(visitId).orElseThrow(() -> APIException.notFound("Visit payment details identified by {0} not available", visitId));
    }
}

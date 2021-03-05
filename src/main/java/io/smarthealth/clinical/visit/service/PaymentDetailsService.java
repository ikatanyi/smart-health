package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.PaymentDetailsRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.Optional;
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

    @Transactional(rollbackFor = Exception.class)
    public PaymentDetails createPaymentDetails(PaymentDetails paymentDetails) {
        return paymentDetailsRepository.save(paymentDetails);
    }

    public PaymentDetails fetchPaymentDetailsByVisit(Long visitId) {
        return paymentDetailsRepository.findById(visitId).orElseThrow(() -> APIException.notFound("Visit payment details identified by {0} not available", visitId));
    }

    public PaymentDetails fetchPaymentDetailsByVisit(Visit visit) {
        return paymentDetailsRepository.findByVisit(visit)
                .orElse(null);
//                .orElseThrow(() -> APIException.notFound("Visit payment details identified by visit number {0} not available", visit.getVisitNumber()));
    }

    public Optional<PaymentDetails> fetchPaymentDetailsByVisitWithoutNotFoundDetection(Visit visit) {
        return paymentDetailsRepository.findByVisit(visit);
    }

    public Optional<PaymentDetails> getPaymentDetailsByVist(Visit visit) {
        return paymentDetailsRepository.findByVisit(visit);
    }

    public Optional<PaymentDetails> getLastPaymentDetailsByPatient(Patient patient) {
        return paymentDetailsRepository.findFirstByPatientOrderByIdDesc(patient);
    }

    @Transactional
    public void deletePaymentDetails(PaymentDetails pd) {
        paymentDetailsRepository.delete(pd);
    }
}

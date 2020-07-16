package io.smarthealth.administration.finances.service;

import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.administration.finances.domain.PaymentTermsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PaymentTermsService {

    private final PaymentTermsRepository paymentTermsRepository;

    public PaymentTermsService(PaymentTermsRepository paymentTermsRepository) {
        this.paymentTermsRepository = paymentTermsRepository;
    }

    @Transactional
    public PaymentTerms createPaymentTerm(PaymentTerms terms) {
        return paymentTermsRepository.save(terms);
    }
    
     @Transactional
    public PaymentTerms updatePaymentTerm(Long id, PaymentTerms terms) {
        PaymentTerms paymentTerms = getPaymentTermByIdWithFailDetection(id);
        paymentTerms.setActive(terms.getActive());
        paymentTerms.setCreditDays(terms.getCreditDays());
        paymentTerms.setDescription(terms.getDescription());
        paymentTerms.setTermsName(terms.getTermsName());
        return paymentTermsRepository.save(paymentTerms);
    }

    public PaymentTerms getPaymentTermByIdWithFailDetection(Long id) {
        return paymentTermsRepository.findById(id).orElseThrow(() -> APIException.notFound("Payment terms identified by id {0} not found ", id));
    }

    public PaymentTerms getPaymentTermByNameWithFailDetection(String id) {
        return paymentTermsRepository.findByTermsName(id).orElseThrow(() -> APIException.notFound("Payment terms identified by name {0} not found ", id));
    }

    public Optional<PaymentTerms> getPaymentTerm(Long id) {
        return paymentTermsRepository.findById(id);
    }

    public Optional<PaymentTerms> getPaymentTermByName(String term) {
        return paymentTermsRepository.findByTermsName(term);
    }

    public Page<PaymentTerms> getPaymentTerms(Pageable page, boolean includeClosed) {

        if (includeClosed) {
            return paymentTermsRepository.findAll(page);
        }
        return paymentTermsRepository.findByActiveTrue(page);
    }

    public List<PaymentTerms> getPaymentTerms() {
        return paymentTermsRepository.findAll();
    }
    
    public List<PaymentTerms> getPaymentTermsByName(String name) {
        return paymentTermsRepository.findByTermsNameContainingIgnoreCase(name);
    }
}

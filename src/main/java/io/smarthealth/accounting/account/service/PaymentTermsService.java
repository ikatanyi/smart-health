package io.smarthealth.accounting.account.service;

import io.smarthealth.administration.app.domain.PaymentTerms;
import io.smarthealth.administration.app.domain.PaymentTermsRepository;
import io.smarthealth.infrastructure.exception.APIException;
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

    public PaymentTerms getPaymentTermByIdWithFailDetection(Long id) {
        return paymentTermsRepository.findById(id).orElseThrow(() -> APIException.notFound("Payment terms identified by id {0} not found ", id));
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
}

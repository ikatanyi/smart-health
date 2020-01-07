package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.data.PaymentMethodData;
import io.smarthealth.accounting.payment.domain.PaymentMethod;
import io.smarthealth.accounting.payment.domain.PaymentMethodRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    public PaymentMethodService(PaymentMethodRepository repository) {
        this.repository = repository;
    }

    public PaymentMethodData createPaymentMethod(PaymentMethodData modeData) {
        PaymentMethod paymode = new PaymentMethod();
        paymode.setName(modeData.getName());
        paymode.setDescription(modeData.getDescription());
        paymode.setIsCashPayment(modeData.getIsCashPayment());

        PaymentMethod result = repository.save(paymode);
        return result.toData();
    }

    public PaymentMethod getPaymentMethod(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> APIException.notFound("Payment Mode with id {0} not found", id));
    }
    
      public Optional<PaymentMethod> getPaymentMethodByName(String paymentName) {
        return repository
                .findByName(paymentName);
    }

    public Page<PaymentMethodData> listPaymentMethods(Pageable page) {
        return repository
                .findAll(page)
                .map(sd -> sd.toData());
    }

    public PaymentMethodData updatePaymentMethod(Long id, PaymentMethodData data) {
        PaymentMethod point = getPaymentMethod(id);
        if (!Objects.equals(point.getActive(), data.getActive())) {
            point.setActive(data.getActive());
        }
        if (!point.getName().equals(data.getName())) {
            point.setName(data.getName());
        }
        if (!point.getDescription().equals(data.getDescription())) {
            point.setName(data.getDescription());
        }
        if(!Objects.equals(point.getIsCashPayment(), data.getIsCashPayment())){
            point.setIsCashPayment(data.getIsCashPayment());
        }
        
        
        PaymentMethod savedPoint = repository.save(point);

        return savedPoint.toData();
    }
}

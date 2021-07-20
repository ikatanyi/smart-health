package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.billing.data.PreAuthData;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.PaymentDetailsRepository;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreAuthorizationService {
    private final PaymentDetailsRepository paymentDetailsRepository;

    public PaymentDetails createPreAuthorization(PreAuthData data){
        Optional<PaymentDetails> optionalPaymentDetails = paymentDetailsRepository.findByVisitNumber(data.getVisitNumber());
        if(optionalPaymentDetails.isPresent()){
            PaymentDetails paymentDetails = optionalPaymentDetails.get();
            paymentDetails.setPreauthCode(data.getPreauthCode());
            paymentDetails.setPreauthRequestedAmount(data.getRequestedAmount() !=null ? data.getRequestedAmount() : BigDecimal.ZERO);
            paymentDetails.setPreauthApprovedAmount(data.getApprovedAmount() !=null ? data.getApprovedAmount() : BigDecimal.ZERO);
            paymentDetails.setPreauthCode(data.getPreauthCode());
            if(data.getApprovedAmount() !=null || data.getApprovedAmount() != BigDecimal.ZERO){
             BigDecimal newRunning = (data.getApprovedAmount().subtract(BigDecimal.valueOf(paymentDetails.getLimitAmount()))).add(BigDecimal.valueOf(paymentDetails.getRunningLimit()));

                paymentDetails.setComments(""+paymentDetails.getLimitAmount());
               paymentDetails.setLimitAmount(data.getApprovedAmount().doubleValue());
               paymentDetails.setCapitationAmount(data.getApprovedAmount());
                paymentDetails.setRunningLimit(newRunning!=null ? newRunning.doubleValue() : 0D);
            }
           return paymentDetailsRepository.save(paymentDetails);

        }
        return null;
    }
}

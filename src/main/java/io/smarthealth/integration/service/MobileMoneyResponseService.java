package io.smarthealth.integration.service;

import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.domain.MobileMoneyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MobileMoneyResponseService {
    private final MobileMoneyResponseRepository moneyResponseRepository;

    public MobileMoneyResponse updateResponse(MobileMoneyResponse response) {
        response.setPatientBillEffected(response.getPatientBillEffected());
        return moneyResponseRepository.save(response);
    }
}

package io.smarthealth.integration.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.integration.data.MobileMoneyProcessingData;
import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.domain.MobileMoneyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MobileMoneyProcessingService {
    private final MobileMoneyResponseRepository moneyResponseRepository;
//    private final

    public MobileMoneyResponse updateResponse(MobileMoneyResponse response) {
        response.setPatientBillEffected(response.getPatientBillEffected());
        return moneyResponseRepository.save(response);
    }

    public MobileMoneyResponse findRecentByPhoneNumber(String phoneNumber) {
        return moneyResponseRepository.findTopByPhoneNoAndPatientBillEffectedOrderByIdDesc(phoneNumber,
                Boolean.FALSE).orElseThrow(() -> APIException.notFound("No active record found"));
    }

    public MobileMoneyResponse findRecentByPhoneNumberOrNull(String phoneNumber) {
        return moneyResponseRepository.findTopByPhoneNoAndPatientBillEffectedOrderByIdDesc(phoneNumber,
                Boolean.FALSE).orElse(null);
    }


//    public void processMobileMoney(MobileMoneyProcessingData data) {
//        //validate mpesa transactions
//        MobileMoneyResponse activeRecord = this.findRecentByPhoneNumber(data.getPhoneNumber());
//
//        //visit based bill processing
//        if (data.getVisitNumber() != null) {
//
//        }
//
//    }
}

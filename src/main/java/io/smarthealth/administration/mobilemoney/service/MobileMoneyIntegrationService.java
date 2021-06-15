package io.smarthealth.administration.mobilemoney.service;

import io.smarthealth.administration.mobilemoney.data.MobileMoneyIntegrationData;
import io.smarthealth.administration.mobilemoney.domain.BusinessNumberType;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegrationRepository;
import io.smarthealth.infrastructure.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MobileMoneyIntegrationService {
    private final MobileMoneyIntegrationRepository mobileMoneyIntegrationRepository;

    public MobileMoneyIntegration save(MobileMoneyIntegrationData data) {
        return mobileMoneyIntegrationRepository.save(MobileMoneyIntegrationData.map(data));
    }

    public MobileMoneyIntegration updateMIP(MobileMoneyIntegrationData data, Long id) {
        MobileMoneyIntegration moneyIntegration =
                mobileMoneyIntegrationRepository.findById(id).orElseThrow(() -> APIException.notFound("Not found"));
        moneyIntegration.setMobileMoneyName(data.getMobileMoneyName());
        moneyIntegration.setAppKey(data.getAppKey());
        moneyIntegration.setAppSecret(data.getAppSecret());
        moneyIntegration.setBusinessNumber(data.getBusinessNumber());
        moneyIntegration.setBusinessNumberType(data.getBusinessNumberType());
        moneyIntegration.setCallBackUrl(data.getCallBackUrl());
        moneyIntegration.setConfirmUrl(data.getConfirmUrl());
        moneyIntegration.setPassKey(data.getPassKey());
        return mobileMoneyIntegrationRepository.save(moneyIntegration);
    }

    public MobileMoneyIntegration findMobileMoneyByProviderNameAndBillType(String providerName,
                                                                           BusinessNumberType businessNumberType
    ) {
        return mobileMoneyIntegrationRepository.findByMobileMoneyNameAndBusinessNumberType(providerName,
                businessNumberType).orElseThrow(() -> APIException.notFound("No mobile money integration configuration" +
                " with params specified"));
    }

    public List<MobileMoneyIntegration> findAll() {
        return mobileMoneyIntegrationRepository.findAll();
    }
}

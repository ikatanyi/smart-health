package io.smarthealth.notification.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notification.data.SMSConfigurationData;
import io.smarthealth.notification.domain.SMSConfiguration;
import io.smarthealth.notification.domain.SMSConfigurationRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SMSConfigurationService {
    private final SMSConfigurationRepository smsConfigurationRepository;

    public SMSConfiguration saveConfiguration(SMSConfigurationData smsConfigurationData) {
        if (smsConfigurationData.getStatus().equals("Active") || smsConfigurationData.getStatus().equals("InActive")) {

        } else {
            throw APIException.badRequest("Status can only be Active/InActive");
        }
        if (smsConfigurationData.getStatus().equals("Active")) {
            List<SMSConfiguration> smsConfigurations = findAllByStatus("Active");
            if (smsConfigurations.size() > 0) {
                throw APIException.conflict("There is another provider set as Active. Deactivate one if you wish to " +
                        "proceed");
            }
        }
        return smsConfigurationRepository.save(SMSConfigurationData.map(smsConfigurationData));
    }

    public SMSConfiguration updateSmsprovider(SMSConfigurationData smsConfigurationData, Long id) {
        SMSConfiguration e = this.findById(id);
        e.setApiKey(smsConfigurationData.getApiKey());
        e.setStatus(smsConfigurationData.getStatus());
        e.setGatewayUrl(smsConfigurationData.getGatewayUrl());
        e.setUsername(smsConfigurationData.getUsername());
        e.setSenderId(smsConfigurationData.getSenderId());
        e.setProviderName(smsConfigurationData.getProviderName());
        if (smsConfigurationData.getStatus().equals("Active") || smsConfigurationData.getStatus().equals("InActive")) {

        } else {
            throw APIException.badRequest("Status can only be Active/InActive");
        }
        if (smsConfigurationData.getStatus().equals("Active")) {
            List<SMSConfiguration> smsConfigurations = findAllByStatus("Active");
            for (SMSConfiguration c : smsConfigurations) {
                if (c.getStatus().equals("Active") && c.getId() != id) {
                    throw APIException.conflict("There is another provider set as Active. Deactivate one if you wish to " +
                            "proceed");
                }
            }
        }
        return smsConfigurationRepository.save(e);
    }

    public List<SMSConfiguration> findAll() {
        return smsConfigurationRepository.findAll();
    }

    public List<SMSConfiguration> findAllByStatus(String status) {
        return smsConfigurationRepository.findByStatus(status);
    }

    public SMSConfiguration findByProviderName(String providerName) {
        return smsConfigurationRepository.findByProviderName(providerName).orElseThrow(() -> APIException.notFound(
                "Provider identified by name {0} not found", providerName));
    }

    public SMSConfiguration findById(Long id) {
        return smsConfigurationRepository.findById(id).orElseThrow(() -> APIException.notFound(
                "Provider identified by id {0} not found", id));
    }
}

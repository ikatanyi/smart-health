package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.data.ExternalServicesData;
import io.smarthealth.administration.config.data.ExternalServicesPropertiesData;
import io.smarthealth.administration.config.domain.ExternalServiceRepository;
import io.smarthealth.administration.config.domain.ExternalServicesProperties;
import io.smarthealth.administration.config.domain.ExternalServicesPropertiesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.administration.config.domain.ExternalService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalServicesConfigurationService {

    private final ExternalServicesPropertiesRepository repository;
    private final  ExternalServiceRepository externalServiceRepository;

     

    public ExternalServicesProperties findOneByIdAndName(Long id, String name, String externalServiceName) {
        final ExternalServicesProperties externalServicesProperties = this.repository.findOneByExternalServicePropertiesPK(name, id);
        if (externalServicesProperties == null) {
            throw APIException.notFound("Parameter `" + name + "` does not exist for the ServiceName `" + externalServiceName + "`");
        }
        return externalServicesProperties;
    }

    public Collection<ExternalServicesPropertiesData> retrieveOne(String serviceName) {
        // this we need to have a start way of accepting the various parameters 
        //representing SMS/ EMAIL /Mpesa/SmartIntegration|
        return new ArrayList<>();
    }

    public ExternalServicesData getExternalServiceDetailsByServiceName(String serviceName) {
        Optional<ExternalService> es = externalServiceRepository.findByName(serviceName);
        if(es.isPresent()){
            return ExternalServicesData.of(es.get());
        }
        return new ExternalServicesData(1L, "");
    }

    public ExternalServicesProperties updateExternalServicesProperties(String externalServiceName, ExternalServicesPropertiesData data) {

        return null;
    }
}

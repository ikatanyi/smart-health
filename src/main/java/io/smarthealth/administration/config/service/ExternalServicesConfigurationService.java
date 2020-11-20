package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.data.ExternalServicesData;
import io.smarthealth.administration.config.data.ExternalServicesPropertiesData;
import io.smarthealth.administration.config.domain.ExternalServicesProperties;
import io.smarthealth.administration.config.domain.ExternalServicesPropertiesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class ExternalServicesConfigurationService {

    private final ExternalServicesPropertiesRepository repository;

    public ExternalServicesConfigurationService(ExternalServicesPropertiesRepository repository) {
        this.repository = repository;
    }

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
        return null;
    }

    public ExternalServicesProperties updateExternalServicesProperties(String externalServiceName, ExternalServicesPropertiesData data) {

        return null;
    }
}

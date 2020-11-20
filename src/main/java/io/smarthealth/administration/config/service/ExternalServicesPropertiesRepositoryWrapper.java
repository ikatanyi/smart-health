package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.domain.ExternalServicesProperties;
import io.smarthealth.administration.config.domain.ExternalServicesPropertiesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.stereotype.Service;

@Service
public class ExternalServicesPropertiesRepositoryWrapper {

    private final ExternalServicesPropertiesRepository repository;

    public ExternalServicesPropertiesRepositoryWrapper(final ExternalServicesPropertiesRepository repository) {
        this.repository = repository;
    }

    public ExternalServicesProperties findOneByIdAndName(Long id, String name, String externalServiceName) {
        final ExternalServicesProperties externalServicesProperties = this.repository.findOneByExternalServicePropertiesPK(name, id);
        if (externalServicesProperties == null) {
            throw APIException.notFound("Parameter `" + name + "` does not exist for the ServiceName `" + externalServiceName + "`");
        }
        return externalServicesProperties;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.domain.GlobalConfigurationRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class ConfigService {

    private final GlobalConfigurationRepository configRepository;

    public ConfigService(GlobalConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }

    public GlobalConfiguration createConfigs(GlobalConfiguration config) {
        return configRepository.save(config);
    }

    public Page<GlobalConfiguration> fetchAllConfigs(Pageable page) {
        return configRepository.findAll(page);
    }

    public Optional<GlobalConfiguration> getConfigs(Long id) {
        return configRepository.findById(id);
    }

    public GlobalConfiguration getWithNonFoundDetection(Long id) {
        return getConfigs(id)
                .orElseThrow(() -> APIException.notFound("Configuration with id  {0} not found.", id));
    }

    public GlobalConfiguration getByNameOrThrow(String config) {
        return findByName(config)
                .orElseThrow(() -> APIException.notFound("Configuration with name  {0} not found.", config));
    }

    public Optional<GlobalConfiguration> findByName(String config) {
        return configRepository.findByName(config);
    }

    public GlobalConfiguration updateConfig(Long id, GlobalConfiguration newConfig) {
        GlobalConfiguration existingConfig = getWithNonFoundDetection(id);

        existingConfig.setValue(newConfig.getValue());
        existingConfig.setDescription(newConfig.getDescription());
        existingConfig.setEnabled(newConfig.isEnabled());
        return configRepository.save(existingConfig);
    }
}

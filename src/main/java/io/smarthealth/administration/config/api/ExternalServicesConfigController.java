/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.api;

import io.smarthealth.administration.config.data.ExternalServicesPropertiesData;
import io.smarthealth.administration.config.service.ExternalServicesConfigurationService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api/externalservice")
public class ExternalServicesConfigController {

    private final ExternalServicesConfigurationService service;
    private final AuditTrailService auditTrailService;

    public ExternalServicesConfigController(ExternalServicesConfigurationService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @GetMapping("{servicename}")
    public Collection<ExternalServicesPropertiesData> retrieveOne(@PathVariable("servicename") final String serviceName) {
        auditTrailService.saveAuditTrail("Administration", "Viewed service "+serviceName);
        return service.retrieveOne(serviceName);
    }

    @PutMapping("{servicename}")
    public List<ExternalServicesPropertiesData> updateExternalServiceProperties(@PathVariable("servicename") final String serviceName, List<ExternalServicesPropertiesData> data) {
        auditTrailService.saveAuditTrail("Administration", "Edited service "+serviceName);
        return data;
    }
}

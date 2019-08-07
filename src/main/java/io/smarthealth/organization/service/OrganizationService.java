    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.domain.Organization;
import io.smarthealth.organization.domain.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author simon.waweru
 */
@Service
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    public Organization findOrganizationOrThrow(String organizationCode) {
        return this.organizationRepository.findByCode(organizationCode)
                .orElseThrow(() -> APIException.notFound("Organization identified by code {0} not found.", organizationCode));
    }
}

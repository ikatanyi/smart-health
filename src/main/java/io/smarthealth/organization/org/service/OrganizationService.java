package io.smarthealth.organization.org.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.org.data.OrganizationData;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.org.domain.OrganizationRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository orgRepository;

    @Transactional
    public OrganizationData createOrganization(OrganizationData orgData) {
        Organization org = OrganizationData.map(orgData);

        OrganizationData savedData = OrganizationData.map(orgRepository.save(org));
        return savedData;
    }

    public Optional<Organization> getOptionalOrganization(String orgId) {
        return orgRepository.findById(orgId);
    }
    
    public Organization getOrganization(String orgId) {
        return orgRepository.findById(orgId)
                .orElseThrow(() -> APIException.notFound("Organization with Id {0} not Found", orgId));
    }
   @Transactional
    public OrganizationData updateOrganization(String id, OrganizationData data) {
        Organization org = getOrganization(id);

        org.setOrganizationName(data.getOrganizationName());
        org.setTaxNumber(data.getTaxNumber());

        Organization savedOrg = orgRepository.save(org);
        return OrganizationData.map(savedOrg);
    }

}

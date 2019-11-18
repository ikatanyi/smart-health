package io.smarthealth.organization.facility.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.domain.FacilityRepository;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.org.service.OrganizationService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final OrganizationService orgService;

    public FacilityService(FacilityRepository facilityRepository, OrganizationService orgService) {
        this.facilityRepository = facilityRepository;
        this.orgService = orgService;
    }

    @Transactional
    public Facility createFacility(String id, FacilityData facilityData) {
        Organization org = orgService.getOrganization(id);
        Facility facility = new Facility();
        if (facilityData.getParentFacilityId() != null) {
            Optional<Facility> pf = getFacility(facilityData.getParentFacilityId());
            if (pf.isPresent()) {
                facility.setParentFacility(pf.get());
            }
        }
        facility.setFacilityType(facilityData.getFacilityType());
        facility.setTaxNumber(facilityData.getTaxNumber());
        facility.setFacilityClass(facilityData.getFacilityClass());
        facility.setFacilityName(facilityData.getFacilityName());
        facility.setEnabled(facilityData.isEnabled());
        facility.setLogo(facilityData.getLogo());
        facility.setOrganization(org);
        facility.setRegistrationNumber(facilityData.getRegistrationNumber());
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(Facility facility, FacilityData facilityData) {
        if (facilityData.getParentFacilityId() != null) {
            Optional<Facility> pf = getFacility(facilityData.getParentFacilityId());
            if (pf.isPresent()) {
                facility.setParentFacility(pf.get());
            }
        }
        facility.setFacilityType(facilityData.getFacilityType());
        facility.setTaxNumber(facilityData.getTaxNumber());
        facility.setFacilityClass(facilityData.getFacilityClass());
        facility.setFacilityName(facilityData.getFacilityName());
        facility.setEnabled(facilityData.isEnabled());
        facility.setLogo(facilityData.getLogo());
        facility.setRegistrationNumber(facilityData.getRegistrationNumber());
        return facilityRepository.save(facility);
    }

    public Optional<Facility> getFacility(Long id) {
        return facilityRepository.findById(id);
    }

    public Facility findFacility(Long id) {
        return getFacility(id)
                .orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", id));
    }

    public Page<Facility> getAllFacilities(Pageable page) {
        return facilityRepository.findAll(page);
    }

//    public Facility fetchFacilityCode(String facilityCode) {
//        return facilityRepository.findByCode(facilityCode).orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", facilityCode));
//    }
    public Facility findFacility(String orgId, Long id) {
        orgService.getOrganization(orgId);
        Facility facility = getFacility(id).orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", id));
        return facility;
    }

    public List<Facility> findByOrganization(String orgId) {
        Organization org = orgService.getOrganization(orgId);

        return org.getFacilities();
    }

    public Facility loggedFacility() {
        return getFacility(Long.valueOf("1"))
                .orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", Long.valueOf("1")));
    }

}

package io.smarthealth.organization.facility.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.exception.FileStorageException;
import io.smarthealth.organization.company.domain.CompanyLogo;
import io.smarthealth.organization.company.domain.CompanyLogoRepository;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.domain.FacilityRepository;
import io.smarthealth.organization.org.domain.Organisation;
import io.smarthealth.organization.org.service.OrganisationService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kelsas
 */
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final OrganisationService orgService;
    private final CompanyLogoRepository logoRepository;

    public FacilityService(FacilityRepository facilityRepository, OrganisationService orgService, CompanyLogoRepository logoRepository) {
        this.facilityRepository = facilityRepository;
        this.orgService = orgService;
        this.logoRepository = logoRepository;
    }

    @Transactional
    public Facility createFacility(String id, FacilityData facilityData) throws IOException {
        byte[] bytes = null;
        Organisation org = orgService.getOrganization(id);
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
        facility.setFooterMsg(facilityData.getFooterMsg());

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
        facility.setFooterMsg(facilityData.getFooterMsg());
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
        Organisation org = orgService.getOrganization(orgId);

        return org.getFacilities();
    }

    public Facility loggedFacility() {
        //find any organization one
        Optional<Facility> facility = facilityRepository.findAll().stream().findAny();
        return facility
                .orElseThrow(() -> APIException.notFound("No facility is configured, Register a Facility to proceed"));
//        return getFacility(Long.valueOf("1"))
//                .orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", Long.valueOf("1")));
    }

    public CompanyLogo storeLogo(Long facilityId, MultipartFile file) {
        // Normalize file name
        Facility facility = findFacility(facilityId);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            if (facility.getCompanyLogo() == null) {
                facility.addLogo(new CompanyLogo(fileName, file.getContentType(), file.getBytes()));
            } else {
                facility.getCompanyLogo().setFileName(fileName);
                facility.getCompanyLogo().setData(file.getBytes());
                facility.getCompanyLogo().setFileType(file.getContentType());
                facility.getCompanyLogo().setFacility(facility);
            }
            return facilityRepository.save(facility).getCompanyLogo();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public CompanyLogo getLogo(Long fileId) {
        return logoRepository.findById(fileId)
                .orElseThrow(() -> APIException.notFound("Logo not found with id " + fileId));
    }

    public void deleteLogo(Long logoId) {
        CompanyLogo logo = getLogo(logoId);
        logoRepository.delete(logo);

    }

}

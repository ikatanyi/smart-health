/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.company.service;

import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.exception.FileStorageException;
import io.smarthealth.organization.company.data.CompanyData;
import io.smarthealth.organization.company.domain.Company;
import io.smarthealth.organization.company.domain.CompanyLogo;
import io.smarthealth.organization.company.domain.CompanyLogoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.organization.company.domain.CompanyRepository;
import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;
    private final CompanyLogoRepository logoRepository;
    private final AdminService adminService;

    @Transactional
    public Company createOrganization(CompanyData data) {
        //TODO get the current tenant id and set it as a company id

        Company org = new Company();
//        org.setLogo(data.getLogo());
        org.setName(data.getName());
        org.setLocation(data.getLocation());
        org.setTaxId(data.getTaxId());
        org.setCompanyId(data.getCompanyId());
        org.setDefaultLanguage(data.getLanguage());
        org.setTimeZone(data.getTimeZone());
        org.setDateFormat(data.getDateFormat());
        org.setContactEmail(data.getContactEmail());
        org.setContactName(data.getContactName());

        org.setCurrency(data.getCurrency());
        if (data.getAddress() != null && data.getAddress().getLine1() != null) {
            Address addresses = adminService.createAddress(data.getAddress());
            addresses.setType(Address.Type.Current);
            org.setAddress(addresses);
        } 
        return repository.save(org);
    }

    public Optional<Company> getOrganization(String orgId) {
        return repository.findById(orgId);
    }

    public Company getOrganizationOrThrow(String orgId) {
        return getOrganization(orgId)
                .orElseThrow(() -> APIException.notFound("Organization with id {0} Not Found", orgId));
    }
    public Company getCurrentOrganization() {
        return repository.findFirstByOrderByCreatedOn()
                .orElseThrow(() -> APIException.notFound("No Current Company configured"));
    }

    public Company updateOrganization(String orgId, CompanyData data) {
        System.err.println(data.toString());
        Company org = getOrganizationOrThrow(orgId);

//        org.setLogo(data.getLogo());
        org.setName(data.getName());
        org.setLocation(data.getLocation());
        org.setTaxId(data.getTaxId());
        org.setCompanyId(data.getCompanyId());
        org.setDefaultLanguage(data.getLanguage());
        org.setTimeZone(data.getTimeZone());
        org.setDateFormat(data.getDateFormat());
        org.setContactEmail(data.getContactEmail());
        org.setContactName(data.getContactName());

        org.setCurrency(data.getCurrency());
        System.err.println("address id "+data.getAddress().getId());
        if (data.getAddress() != null && data.getAddress().getId() != null) {
            Address addresses = adminService.updateAddress(org.getAddress().getId(), data.getAddress());
            org.setAddress(addresses);
        } else {
            Address addresses = adminService.createAddress(data.getAddress());
            addresses.setType(Address.Type.Office);
            org.setAddress(addresses);
        }
 
        return repository.save(org);
    }

    public CompanyLogo storeLogo(String companyId, MultipartFile file) {
        // Normalize file name
//        Company company = getOrganizationOrThrow(companyId);

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            CompanyLogo logo = new CompanyLogo(fileName, file.getContentType(), file.getBytes());
//            logo.setCompany(company);

            return logoRepository.save(logo);
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

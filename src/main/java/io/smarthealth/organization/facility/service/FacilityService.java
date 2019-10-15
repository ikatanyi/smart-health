/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.service;

import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.domain.OrganizationRepository;
import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.domain.FacilityRepository;
import io.smarthealth.organization.service.OrganizationService;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author simon.waweru
 */
@Service
public class FacilityService {

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public Facility createFacility(FacilityData facilityData) {
        try {
            Facility facility = FacilityData.map(facilityData);

           return  facilityRepository.save(facility);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating facility ", e.getMessage());
        }
    }

    public Facility fetchFacilityById(String facilityId) {
        return facilityRepository.findById(facilityId).orElseThrow(() -> APIException.notFound("Facility identified by {0} not found", facilityId));
    }

    public Facility fetchFacilityCode(String facilityCode) {
        return facilityRepository.findByCode(facilityCode).orElseThrow(() -> APIException.notFound("Facility identified by code {0} not found", facilityCode));
    }

    public ContentPage<FacilityData> fetchAllFacilities(Pageable pageable) {
        ContentPage<FacilityData> facilitiesData = new ContentPage<>();
        Page<Facility> facilities = facilityRepository.findAll(pageable);
        facilitiesData.setTotalElements(facilities.getTotalElements());
        facilitiesData.setTotalPages(facilities.getTotalPages());
        if (facilities.getSize() > 0) {
            List<FacilityData> facilityDataList = new ArrayList<>();
            for (Facility facility : facilities) {
                FacilityData facilityData = FacilityData.map(facility);
                facilityDataList.add(facilityData);
            }
            facilitiesData.setContents(facilityDataList);
        }
        return facilitiesData;
    }

    public boolean deleteFacility(Facility facility) {
        try {
            facilityRepository.deleteById(facility.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error deleting facility id " + facility.getId(), e.getMessage());
        }
    }

    public Facility convertFacilityDataToEntity(FacilityData facilityData) {
        Facility facility = modelMapper.map(facilityData, Facility.class);
        return facility;
    }

    public FacilityData convertFacilityEntityToData(Facility facility) {
        FacilityData facilityData = modelMapper.map(facility, FacilityData.class);

        return facilityData;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.organization.data.OrganizationData;
import io.smarthealth.organization.domain.Organization;
import io.smarthealth.organization.facility.domain.Facility;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author simon.waweru
 */
@Data
public class FacilityData {
    
    @Enumerated
    private Facility.Type facilityType;
    
    private Boolean parent;
    private String registration_number;
    private String facilityClass;
    
    private String code;
    private String name;
    private String taxId;
    private String website;
    private String country;
    private Boolean enabled;
    private byte[] logo;
    
    private Long creditLimitId;
    
    public static FacilityData map(Facility facility) {
        FacilityData facilityData = new FacilityData();
        facilityData.setCode(facility.getCode());
        facilityData.setCountry(facility.getCountry());
        facilityData.setCreditLimitId(facility.getCreditLimit().getId());
        facilityData.setEnabled(facility.getEnabled());
        facilityData.setFacilityClass(facility.getFacilityClass());
        facilityData.setFacilityType(Facility.Type.valueOf(facility.getFacilityType()));
        facilityData.setName(facility.getName());
        facilityData.setParent(Boolean.TRUE);
        facilityData.setRegistration_number(facility.getRegistrationNumber());
        facilityData.setTaxId(facility.getTaxId());
        facilityData.setWebsite(facility.getWebsite());
        facilityData.setLogo(facility.getLogo());
        return facilityData;
    }
    
    public static Facility map(FacilityData facilityData) {
        Facility facility = new Facility();
        facility.setCode(facilityData.getCode());
        facility.setCountry(facilityData.getCountry());
        facility.setEnabled(facilityData.getEnabled());
        facility.setFacilityClass(facilityData.getFacilityClass());
        facility.setFacilityType(facilityData.getFacilityType().name());
        facility.setName(facilityData.getName());
        facility.setParent(facilityData.getParent());
        facility.setRegistrationNumber(facilityData.getRegistration_number());
        facility.setTaxId(facilityData.getTaxId());
        facility.setWebsite(facilityData.getWebsite());
        facility.setLogo(facilityData.getLogo());
        return facility;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.organization.facility.domain.Facility;
import lombok.Data;

import javax.persistence.Lob;

/**
 *
 * @author simon.waweru
 */
@Data
public class FacilityData {
    
    private String organizationId;
    private String organizationName;
    private String registrationNumber;
    private String facilityType;
    private String taxNumber;
    private String facilityClass; //government classifications
    private String facilityName;
    private Long facilityId;
    private Long parentFacilityId;
    private String parentFacility;
    @Lob
    private byte[] logo;
    private boolean enabled;
    
    public static FacilityData map(Facility facility) {
        FacilityData facilityData = new FacilityData();
        if (facility.getOrganization() != null) {
            facilityData.setOrganizationId(facility.getOrganization().getId());
            facilityData.setOrganizationName(facilityData.getOrganizationName());
        }
        if (facility.getParentFacility() != null) {
            facilityData.setParentFacility(facility.getParentFacility().getFacilityName());
            facilityData.setParentFacilityId(facility.getParentFacility().getId());
        }        
        facilityData.setFacilityType(facility.getFacilityType());
        facilityData.setTaxNumber(facility.getTaxNumber());
        facilityData.setFacilityClass(facility.getFacilityClass());
        facilityData.setFacilityName(facility.getFacilityName());
        facilityData.setEnabled(facility.isEnabled());
        facilityData.setLogo(facility.getLogo());
        facilityData.setOrganizationName(facility.getOrganization().getOrganizationName());
        facilityData.setRegistrationNumber(facility.getRegistrationNumber());
        facilityData.setFacilityId(facility.getId());
        
        return facilityData;
    }
    
}

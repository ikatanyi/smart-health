/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.organization.facility.domain.Facility;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile file;
    private String logo;
    private String footerMsg;
      private String currency;
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
        if(facility.getCompanyLogo()!=null){
//            new ByteArrayResource(dbFile.getData())
            facilityData.setLogo(encodeImage(facility.getCompanyLogo().getData()));
        }
        
        facilityData.setOrganizationName(facility.getOrganization().getOrganizationName());
        facilityData.setRegistrationNumber(facility.getRegistrationNumber());
        facilityData.setFacilityId(facility.getId());
        facilityData.setFooterMsg(facility.getFooterMsg());
        facilityData.setCurrency(facility.getCurrency());
        
        return facilityData;
    }
    
    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64String(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
    
}

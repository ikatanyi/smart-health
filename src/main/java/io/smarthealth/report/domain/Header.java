/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.domain;

import io.smarthealth.organization.facility.data.FacilityData;
import io.smarthealth.organization.facility.domain.Facility;
import java.io.ByteArrayInputStream;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class Header {

    private String facilityName;
    private String facilityType;
    private ByteArrayInputStream logo;
    private String orgLegalName;
    private String orgName;
    private String taxNumber;
    private String orgWebsite;
    private String orgAddressCountry;
    private String orgAddressCounty;
    private String orgAddressLine1;
    private String orgAddressLine2;
    private String orgPostalCode;
    private String orgTown;
    private String orgType;
    private String contactEmail;
    private String contactFullName;
    private String contactMobile;
    private String salutation;
    private String telephone;

    public static Header map(Facility facility) {
        Header header = new Header();
        if (facility.getOrganization() != null) {
            if (!facility.getOrganization().getContact().isEmpty()) {
                header.setContactEmail(facility.getOrganization().getContact().get(0).getEmail());
                header.setContactFullName(facility.getOrganization().getContact().get(0).getFullName());
                header.setContactMobile(facility.getOrganization().getContact().get(0).getMobile());
                header.setSalutation(facility.getOrganization().getContact().get(0).getSalutation());
                header.setTelephone(facility.getOrganization().getContact().get(0).getTelephone());
                header.setSalutation(facility.getOrganization().getContact().get(0).getSalutation());
                header.setTelephone(facility.getOrganization().getContact().get(0).getTelephone());
            }

            if (facility.getOrganization().getAddress()!=null && !facility.getOrganization().getAddress().isEmpty()) {
                header.setOrgAddressCountry(facility.getOrganization().getAddress().get(0).getCountry());
                header.setOrgAddressCounty(facility.getOrganization().getAddress().get(0).getCounty());
                header.setOrgAddressLine1(facility.getOrganization().getAddress().get(0).getLine1());
                header.setOrgAddressLine2(facility.getOrganization().getAddress().get(0).getLine2());
                header.setOrgPostalCode(facility.getOrganization().getAddress().get(0).getPostalCode());
                header.setOrgTown(facility.getOrganization().getAddress().get(0).getTown());
                header.setOrgType(facility.getOrganization().getAddress().get(0).getType().name());
            }
            header.setOrgLegalName(facility.getOrganization().getLegalName());
            header.setOrgName(facility.getOrganization().getOrganizationName());
            header.setOrgWebsite(facility.getOrganization().getWebsite());
            header.setTaxNumber(facility.getOrganization().getTaxNumber());
            header.setFacilityName(facility.getFacilityName());
            header.setFacilityType(facility.getFacilityType());
            
        }

        return header;
    }
}

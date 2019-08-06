/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.data;

import io.smarthealth.organization.domain.Organization;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author simon.waweru
 */
@Data
public class OrganizationData {

    private String code;
    private String name;
    private String taxId;
    private String website;
    private String country;
    private Boolean enabled;

    private Long creditLimitId;

    public static Organization map(final OrganizationData orgData) {
        Organization org = new Organization();
        org.setCode(orgData.getCode());
        org.setCountry(org.getCountry());
        org.getCreditLimit().setId(orgData.getCreditLimitId());
        org.setEnabled(orgData.getEnabled());
        org.setName(orgData.getName());
        org.setTaxId(orgData.getTaxId());
        org.setWebsite(orgData.getWebsite());
        return org;
    }

    public static OrganizationData map(Organization org) {
        OrganizationData orgData = new OrganizationData();
        orgData.setCode(org.getCode());
        orgData.setCountry(org.getCountry());
        orgData.setCreditLimitId(org.getCreditLimit().getId());
        orgData.setEnabled(org.getEnabled());
        orgData.setName(org.getName());
        orgData.setTaxId(org.getTaxId());
        orgData.setWebsite(org.getWebsite());
        return orgData;
    }

}

package io.smarthealth.organization.org.data;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.facility.domain.*;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class OrganizationData {

    private String id;
    private String organizationType;
    private String organizationName;
    private String legalName;
    private String taxNumber;
    private String website;
    private String country;
    private Boolean active;
    private List<AddressData> addresses;
    private List<ContactData> contacts;

    public static OrganizationData map(Organization org) {
        OrganizationData data = new OrganizationData();
        data.setId(org.getId());
        data.setOrganizationName(org.getOrganizationName());
        data.setOrganizationType(org.getOrganizationType().name());
        data.setLegalName(org.getLegalName());
        data.setTaxNumber(org.getTaxNumber());
        data.setWebsite(org.getWebsite());
        data.setCountry(org.getCountry());
       

        return data;
    }

    public static Organization map(OrganizationData orgData) {
        Organization org = new Organization();
        org.setId(orgData.getId());
        org.setOrganizationName(orgData.getOrganizationName());
        org.setOrganizationType(Organization.Type.valueOf(orgData.getOrganizationType()));
        org.setLegalName(orgData.getLegalName());
        org.setTaxNumber(orgData.getTaxNumber());
        org.setWebsite(orgData.getWebsite());
        org.setCountry(orgData.getCountry());

        return org;
    }
}

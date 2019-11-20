package io.smarthealth.organization.org.data;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.organization.org.domain.Organization;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(required = false, hidden = true)
    private Boolean active;

    private String line1;
    private String line2;
    private String town;
    private String county;
    private String country;
    private String postalCode;
    private String addressType;

    private String contactSalutation;
    private String contactFullName;
    private String email;
    private String telephone;
    private String mobile;

    @ApiModelProperty(required = false, hidden = true)
    private List<AddressData> addresses;
    @ApiModelProperty(required = false, hidden = true)
    private List<ContactData> contacts;

    public static OrganizationData map(Organization org) {
        OrganizationData data = new OrganizationData();
        data.setId(org.getId());
        data.setOrganizationName(org.getOrganizationName());
        data.setOrganizationType(org.getOrganizationType().name());
        data.setLegalName(org.getLegalName());
        data.setTaxNumber(org.getTaxNumber());
        data.setWebsite(org.getWebsite());

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

        return org;
    }
}

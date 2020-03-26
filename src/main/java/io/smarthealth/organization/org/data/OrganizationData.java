package io.smarthealth.organization.org.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.organization.org.domain.Organization;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonInclude(Include.NON_NULL)
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
    AddressData address;
    @ApiModelProperty(required = false, hidden = true)
    private List<ContactData> contacts;

    @ApiModelProperty(required = false, hidden = true)
    private ContactData contact;

    public static OrganizationData map(Organization org) {
        OrganizationData data = new OrganizationData();
        data.setId(org.getId());
        data.setOrganizationName(org.getOrganizationName());
        data.setOrganizationType(org.getOrganizationType().name());
        data.setLegalName(org.getLegalName());
        data.setTaxNumber(org.getTaxNumber());
        data.setWebsite(org.getWebsite());
        if (!org.getAddress().isEmpty()) {
            for (Address address : org.getAddress()) {
                AddressData addressData = new AddressData();
                addressData.setCountry(address.getCountry());
                addressData.setCounty(address.getCounty());
                addressData.setLine1(address.getLine1());
                addressData.setLine2(address.getLine2());
                addressData.setPostalCode(address.getPostalCode());
                addressData.setTown(address.getTown());
                if(address.getType()!=null){
                    addressData.setType(address.getType().name());
                }
                addressData.setId(address.getId());
                data.setAddress(addressData);
            }
        }
        if (!org.getContact().isEmpty()) {
            for (Contact contact : org.getContact()) {
                ContactData contactData = new ContactData();
                contactData.setId(contact.getId());
                contactData.setEmail(contact.getEmail());
                contactData.setFullName(contact.getFullName());
                contactData.setMobile(contact.getMobile());
                contactData.setSalutation(contactData.getSalutation());
                contactData.setTelephone(contact.getTelephone());
                data.setContact(contactData);
            }
        }

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

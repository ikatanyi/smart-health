package io.smarthealth.organization.org.service;

import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.org.data.OrganizationData;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.org.domain.OrganizationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class OrganizationService {

    private final OrganizationRepository orgRepository;

    private final AdminService adminService;

    public OrganizationService(OrganizationRepository orgRepository, AdminService adminService) {
        this.orgRepository = orgRepository;
        this.adminService = adminService;
    }

    @Transactional
    public OrganizationData createOrganization(OrganizationData o) {

        Optional<Organization> orgx = orgRepository.findTopByOrderByOrganizationNameDesc();
        if (orgx.isPresent()) {
            throw APIException.notFound("Organization Already Exists");
        }

        Organization org = new Organization();
        //check if there is an organization already
        List<Organization> orgs = fetchOrganizations();
        if (orgs.size() > 0) {
            org = orgs.get(0);
            org.setOrganizationName(o.getOrganizationName());
            org.setOrganizationType(Organization.Type.valueOf(o.getOrganizationType()));
            org.setLegalName(o.getLegalName());
            org.setTaxNumber(o.getTaxNumber());
            org.setWebsite(o.getWebsite());
            //delete address
            adminService.removeAddressByOrganization(org);
            //delete contact
            adminService.removeContactByOrganization(org);
        } else {
            org = OrganizationData.map(o);
        }
        org.setActive(Boolean.TRUE);
        Organization savedOrg = orgRepository.save(org);
        //Address data
        List<Address> addresses = new ArrayList<>();
        Address a = new Address();
        a.setCountry(o.getCountry());
        a.setCounty(o.getCounty());
        a.setLine1(o.getLine1());
        a.setLine2(o.getLine2());
        a.setPostalCode(o.getPostalCode());
        a.setTown(o.getTown());
        System.out.println("o.getAddressType() " + o.getAddressType());
        if (o.getAddressType() != null && !o.getAddressType().equals("")) {
            a.setType(Address.Type.valueOf(o.getAddressType()));
        }

        addresses.add(a);

        savedOrg.setAddress(addresses);

        adminService.createAddressesEntity(addresses);

        //Contact data
        List<Contact> contact = new ArrayList<>();
        Contact c = new Contact();
        c.setEmail(o.getEmail());
        c.setFullName(o.getContactFullName());
        c.setMobile(o.getMobile());
        c.setSalutation(o.getContactSalutation());
        c.setTelephone(o.getTelephone());

        contact.add(c);

        savedOrg.setContact(contact);
        adminService.createContactEntity(contact);

        OrganizationData savedData = OrganizationData.map(savedOrg);
        return savedData;
    }

    public List<Organization> fetchOrganizations() {
        return orgRepository.findAll();
    }

    public Optional<Organization> getOptionalOrganization(String orgId) {
        return orgRepository.findById(orgId);
    }

    public Organization getOrganization(String orgId) {
        return orgRepository.findById(orgId)
                .orElseThrow(() -> APIException.notFound("Organization with Id {0} not Found", orgId));
    }

    @Transactional
    public OrganizationData updateOrganization(String id, OrganizationData o) {
        Organization org = getOrganization(id);
        org.setOrganizationName(o.getOrganizationName());
        org.setOrganizationType(Organization.Type.valueOf(o.getOrganizationType()));
        org.setLegalName(o.getLegalName());
        org.setTaxNumber(o.getTaxNumber());
        org.setWebsite(o.getWebsite());
        //delete address
        adminService.removeAddressByOrganization(org);
        //delete contact
        adminService.removeContactByOrganization(org);

        org.setActive(Boolean.TRUE);
        Organization savedOrg = orgRepository.save(org);
        //Address data
        List<Address> addresses = new ArrayList<>();
        Address a = new Address();
        a.setCountry(o.getCountry());
        a.setCounty(o.getCounty());
        a.setLine1(o.getLine1());
        a.setLine2(o.getLine2());
        a.setPostalCode(o.getPostalCode());
        a.setTown(o.getTown());
        a.setType(Address.Type.valueOf(o.getAddressType()));

        addresses.add(a);

        savedOrg.setAddress(addresses);

        adminService.createAddressesEntity(addresses);

        //Contact data
        List<Contact> contact = new ArrayList<>();
        Contact c = new Contact();
        c.setEmail(o.getEmail());
        c.setFullName(o.getContactFullName());
        c.setMobile(o.getMobile());
        c.setSalutation(o.getContactSalutation());
        c.setTelephone(o.getTelephone());

        contact.add(c);

        savedOrg.setContact(contact);
        adminService.createContactEntity(contact);

        OrganizationData savedData = OrganizationData.map(savedOrg);
        return savedData;

    }

    public Organization getActiveOrganization() {
        return orgRepository.findTopByOrderByOrganizationNameDesc()
                .orElseThrow(() -> APIException.notFound("No active Organization set"));
    }

}

package io.smarthealth.organization.org.service;

import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.org.data.OrganisationData;
import io.smarthealth.organization.org.domain.Organisation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.organization.org.domain.OrganisationRepository;

/**
 *
 * @author Kelsas
 */
@Service
public class OrganisationService {

    private final OrganisationRepository orgRepository;

    private final AdminService adminService;

    public OrganisationService(OrganisationRepository orgRepository, AdminService adminService) {
        this.orgRepository = orgRepository;
        this.adminService = adminService;
    }

    @Transactional
    public OrganisationData createOrganization(OrganisationData o) {

        Optional<Organisation> orgx = orgRepository.findTopByOrderByOrganizationNameDesc();
        if (orgx.isPresent()) {
            throw APIException.notFound("Organization Already Exists");
        }

        Organisation org = new Organisation();
        //check if there is an organization already
        List<Organisation> orgs = fetchOrganizations();
        if (orgs.size() > 0) {
            org = orgs.get(0);
            org.setOrganizationName(o.getOrganizationName());
            org.setOrganizationType(Organisation.Type.valueOf(o.getOrganizationType()));
            org.setLegalName(o.getLegalName());
            org.setTaxNumber(o.getTaxNumber());
            org.setWebsite(o.getWebsite());
            //delete address
            adminService.removeAddressByOrganization(org);
            //delete contact
            adminService.removeContactByOrganization(org);
        } else {
            org = OrganisationData.map(o);
        }
        org.setActive(Boolean.TRUE);
        Organisation savedOrg = orgRepository.save(org);
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

        OrganisationData savedData = OrganisationData.map(savedOrg);
        return savedData;
    }

    public List<Organisation> fetchOrganizations() {
        return orgRepository.findAll();
    }

    public Optional<Organisation> getOptionalOrganization(String orgId) {
        return orgRepository.findById(orgId);
    }

    public Organisation getOrganization(String orgId) {
        return orgRepository.findById(orgId)
                .orElseThrow(() -> APIException.notFound("Organization with Id {0} not Found", orgId));
    }

    @Transactional
    public OrganisationData updateOrganization(String id, OrganisationData o) {
        Organisation org = getOrganization(id);
        org.setOrganizationName(o.getOrganizationName());
        org.setOrganizationType(Organisation.Type.valueOf(o.getOrganizationType()));
        org.setLegalName(o.getLegalName());
        org.setTaxNumber(o.getTaxNumber());
        org.setWebsite(o.getWebsite());
        //delete address
        adminService.removeAddressByOrganization(org);
        //delete contact
        adminService.removeContactByOrganization(org);

        org.setActive(Boolean.TRUE);
        Organisation savedOrg = orgRepository.save(org);
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

        OrganisationData savedData = OrganisationData.map(savedOrg);
        return savedData;

    }

    public Organisation getActiveOrganization() {
        return orgRepository.findTopByOrderByOrganizationNameDesc()
                .orElseThrow(() -> APIException.notFound("No active Organization set"));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.partner.service;

import io.smarthealth.financial.account.domain.PaymentTerms;
import io.smarthealth.financial.account.domain.PaymentTermsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.bank.data.BankAccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.contact.domain.Address;
import io.smarthealth.organization.contact.domain.Contact;
import io.smarthealth.organization.partner.data.PartnerData;
import io.smarthealth.organization.partner.domain.Partner;
import io.smarthealth.organization.partner.domain.PartnerRepository;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class PartnerService {

    /*
    1. Create partner
    2. Fetch all partners
    3. Update partners details
    4. Remove/Delete partner
     */
    PartnerRepository partnerRepository;
    PaymentTermsRepository paymentTermsRepository;
    ModelMapper dataMapper;

    public PartnerService(PartnerRepository partnerRepository, ModelMapper dataMapper) {
        this.partnerRepository = partnerRepository;
        this.dataMapper = dataMapper;
    }

    public ContentPage<PartnerData> fetchAllPartners(final Pageable pageable) {
        ContentPage<PartnerData> partnerData = new ContentPage<>();
        Page<Partner> partners = partnerRepository.findAll(pageable);

        partnerData.setTotalElements(partners.getTotalElements());
        partnerData.setTotalPages(partners.getTotalPages());
        if (partners.getSize() > 0) {
            List<PartnerData> partnersData = new ArrayList<>();
            for (Partner partner : partners) {
                partnersData.add(dataMapper.map(partner, PartnerData.class));
            }
            partnerData.setContents(partnersData);
        }

        return partnerData;
    }

    public Partner updatePartner(String code, PartnerData partnerData) {
        try {
            //find partner by code 
            Partner partnerEntity = partnerRepository.findByCode(code).get();
            //find payment terms by credit limit 
            PaymentTerms payTerms = paymentTermsRepository.getOne(partnerData.getCreditLimit());
            partnerEntity.setCode(partnerData.getCode());
            partnerEntity.setCountry(partnerData.getCountry());
            partnerEntity.setCreditLimit(payTerms);
            partnerEntity.setEnabled(partnerData.getEnabled());
            partnerEntity.setName(partnerData.getName());
            partnerEntity.setTaxId(partnerData.getTaxId());
            partnerEntity.setWebsite(partnerData.getWebsite());
            partnerRepository.saveAndFlush(partnerEntity);
            return partnerEntity;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("There was a problem updating partner details", e.getMessage());
        }
    }

    public Partner createPartner(final PartnerData partnerData) {
        try {
            Partner partner = dataMapper.map(partnerData, Partner.class);
            if (partnerData.getAddress().size() > 0) {
                List<Address> addresses = new ArrayList<>();
                for (AddressData addressData : partnerData.getAddress()) {
                    addresses.add(dataMapper.map(addressData, Address.class));
                }
                partner.setAddress(addresses);
            }
            if (partnerData.getContact().size() > 0) {
                List<Contact> contacts = new ArrayList<>();
                for (ContactData contact : partnerData.getContact()) {
                    contacts.add(dataMapper.map(contact, Contact.class));
                }
                partner.setContacts(contacts);
            }
            if (partnerData.getBankAccount().size() > 0) {
                List<BankAccount> bankAccounts = new ArrayList<>();
                for (BankAccountData account : partnerData.getBankAccount()) {
                    bankAccounts.add(BankAccountData.map(account));
                }
                partner.setBankAccount(bankAccounts);
            }
            return partnerRepository.saveAndFlush(partner);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("There was a problem creating partner details ", e.getMessage());
        }
    }

}

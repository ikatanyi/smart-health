/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.partner.data;

import io.smarthealth.organization.bank.data.BankAccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PartnerData {

    private String partnerType;
    private String code;
    private String name;
    private String taxId;
    private String website;
    private String country;
    private Boolean enabled;

    private Long creditLimit;
    
    private List<AddressData> address;
    private List<ContactData> contact;
    private List<BankAccountData> bankAccount;

}

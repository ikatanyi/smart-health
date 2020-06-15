/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.data;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.enumeration.Type;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PayerData {

    private Type payerType;
    private Long payerId;
    private String payerName;
    private String legalName;
    private String taxNumber;
    private String website;
    private Long branchId;
    private Long paymentTermId;
    private Long priceBookId;
    private boolean insurance;
    @ApiModelProperty(hidden = true)
    private String priceBookName;
    @ApiModelProperty(hidden = true)
    private Long bankId;
    @ApiModelProperty(hidden = true)
    private String branchName;

    private String debitAccountNo;
    private String accountNumber;
    private List<AddressData> address;
    private List<ContactData> contact;

    //contact data
    private String salutation;
    private String firstName;
    private String lastName;
    private String contactRole;
    private String email;
    private String telephone;
    private String mobile;

    //for reporting
    @ApiModelProperty(required = false, hidden = true)
    private String county;
    @ApiModelProperty(required = false, hidden = true)
    private String country;
    @ApiModelProperty(required = false, hidden = true)
    private String postalCode;
    @ApiModelProperty(required = false, hidden = true)
    private String addressLine1;
    @ApiModelProperty(required = false, hidden = true)
    private String phone;

    public static PayerData map(final Payer payer) {
        PayerData payerData = new PayerData();
        if(payer.getBankBranch()!=null){
        payerData.setBranchId(payer.getBankBranch().getId());
        payerData.setBranchName(payer.getBankBranch().getBranchName());
        payerData.setBankId(payer.getBankBranch().getId());
        }
        
        if (payer.getDebitAccount() != null) {
            payerData.setDebitAccountNo(payer.getDebitAccount().getIdentifier());
        }
        payerData.setInsurance(payer.isInsurance());
        payerData.setLegalName(payer.getLegalName());
        payerData.setPayerName(payer.getPayerName());
        payerData.setPayerType(payer.getPayerType());
        payerData.setAccountNumber(payer.getAccountNumber());
        if (payer.getPaymentTerms() != null) {
            payerData.setPaymentTermId(payer.getPaymentTerms().getId());
        }
        payerData.setTaxNumber(payer.getTaxNumber());
        payerData.setWebsite(payer.getWebsite());

        if (!payer.getAddress().isEmpty()) {
            List<AddressData> addressDataList = new ArrayList<>();
            payer.getAddress().stream().map((address) -> AddressData.map(address)).forEachOrdered((addressData) -> {
                addressDataList.add(addressData);
            });
            payerData.setAddress(addressDataList);

            payerData.setAddressLine1(payer.getAddress().get(0).getLine1());
            payerData.setEmail(payer.getAddress().get(0).getEmail());
            payerData.setPostalCode(payer.getAddress().get(0).getPostalCode());
            payerData.setPhone(payer.getAddress().get(0).getPhone());
            payerData.setCountry(payer.getAddress().get(0).getCountry());
            payerData.setCounty(payer.getAddress().get(0).getCounty());
        }

        if (!payer.getContacts().isEmpty()) {
            List<ContactData> contactDataList = new ArrayList<>();
            for (Contact contact : payer.getContacts()) {
                ContactData contactData = ContactData.map(contact);
                payerData.setMobile(contact.getMobile());
                contactDataList.add(contactData);
            }
            payerData.setContact(contactDataList);

        }
        payerData.setPayerId(payer.getId());
        if (payer.getPriceBook() != null) {
            payerData.setPriceBookId(payer.getPriceBook().getId());
            payerData.setPriceBookName(payer.getPriceBook().getName());
        }

        return payerData;
    }

    public static Payer map(final PayerData payerData) {
        Payer payer = new Payer();
        payer.setInsurance(payerData.isInsurance());
        payer.setLegalName(payerData.getLegalName());
        payer.setPayerName(payerData.getPayerName());
        payer.setPayerType(payerData.getPayerType());
        payer.setTaxNumber(payerData.getTaxNumber());
        payer.setWebsite(payerData.getWebsite());
        payer.setAccountNumber(payerData.getAccountNumber());
        return payer;
    }

}

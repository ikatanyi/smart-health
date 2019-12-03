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
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PayerData {

    private Payer.Type payerType;
    private String payerName;
    private String legalName;
    private String taxNumber;
    private String website;
    private Long branchId;
    private Long paymentTermId;
    private boolean insurance;
    private String debitAccountNo;
    private List<AddressData> address;
    private List<ContactData> contact;

    public static PayerData map(final Payer payer) {
        PayerData payerData = new PayerData();
        payerData.setBranchId(payer.getBankBranch().getId());
        if (payer.getDebitAccount() != null) {
            payerData.setDebitAccountNo(payer.getDebitAccount().getAccountNumber());
        }
        payerData.setInsurance(payer.isInsurance());
        payerData.setLegalName(payer.getLegalName());
        payerData.setPayerName(payer.getPayerName());
        payerData.setPayerType(payer.getPayerType());
        payerData.setPaymentTermId(payer.getPaymentTerms().getId());
        payerData.setTaxNumber(payer.getTaxNumber());
        payerData.setWebsite(payer.getWebsite());

        if (!payer.getAddress().isEmpty()) {
            List<AddressData> addressDataList = new ArrayList<>();
            payer.getAddress().stream().map((address) -> AddressData.map(address)).forEachOrdered((addressData) -> {
                addressDataList.add(addressData);
            });
            payerData.setAddress(addressDataList);
        }

        if (!payer.getContacts().isEmpty()) {
            List<ContactData> contactDataList = new ArrayList<>();
            for (Contact contact : payer.getContacts()) {
                ContactData contactData = ContactData.map(contact);
                contactDataList.add(contactData);
            }
            payerData.setContact(contactDataList);
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
        return payer;
    }

}

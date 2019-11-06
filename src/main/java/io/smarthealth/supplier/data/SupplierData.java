package io.smarthealth.supplier.data;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.BankAccountData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.supplier.domain.Supplier;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierData {

    private Long id;
    private String type;
    private String supplierName;
    private String legalName;
    private String taxNumber;
    private String website;

    private Long currencyId;
    private String currency;

    private Long pricebookId;
    private String pricebook;

    private Long paymentTermsId;
    private String paymentTerms;

    private BankAccountData bank;
    private List<AddressData> addresses;
    private List<ContactData> contacts;

 //    private String contactName;
//    private String contactEmail;
//    private String contactPhone;
//    private String bankName;
//    private String bankBranch;
//    private String accountName;
//    private String accountNumber;
//    private String swiftNumber;
//    private String addessType;
//    private String addressLine1;
//    private String addressLine2;
//    private String town;
//    private String postalCode;
//    private String email;
//    private String phone;
//    private String county;
//    private String country;
    private String notes;

    public static SupplierData map(Supplier supplier) {
        SupplierData data = new SupplierData();
        data.setId(supplier.getId());
        data.setType(supplier.getSupplierType().name());
        data.setSupplierName(supplier.getSupplierName());
        data.setLegalName(supplier.getLegalName());
        data.setTaxNumber(supplier.getTaxNumber());
        data.setWebsite(supplier.getWebsite());

        if (supplier.getCurrency() != null) {
            data.setCurrencyId(supplier.getCurrency().getId());
            data.setCurrency(supplier.getCurrency().getName());
        }
        if (supplier.getPricelist() != null) {
            data.setPricebookId(supplier.getPricelist().getId());
            data.setPricebook(supplier.getPricelist().getName());
        }

        if (supplier.getPaymentTerms() != null) {
            data.setPaymentTermsId(supplier.getPaymentTerms().getId());
            data.setPaymentTerms(supplier.getPaymentTerms().getTermsName());
        }

        if (supplier.getBankAccount() != null) {
//            BankAccount acc = supplier.getBankAccount();
//            data.setBankName(acc.getBankName());
//            data.setBankBranch(acc.getBankBranch());
//            data.setSwiftNumber(acc.getSwiftNumber());
//            data.setAccountName(acc.getAccountName());
//            data.setAccountNumber(acc.getAccountNumber());
            data.setBank(BankAccountData.map(supplier.getBankAccount()));
        }

        if (supplier.getAddress() != null) {
            List<AddressData> adds = supplier.getAddress()
                    .stream()
                    .map(address -> AddressData.map(address))
                    .collect(Collectors.toList());
            data.setAddresses(adds);
        }

        if (supplier.getContacts() != null) {
            List<ContactData> adds = supplier.getContacts()
                    .stream()
                    .map(contact -> ContactData.map(contact))
                    .collect(Collectors.toList());
            data.setContacts(adds);
        }
        data.setNotes(supplier.getNotes());

        return data;
    }
}

package io.smarthealth.supplier.service;

import io.smarthealth.administration.app.domain.PaymentTerms;
import io.smarthealth.administration.app.domain.PaymentTermsRepository;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.service.PricebookService;
import io.smarthealth.administration.app.data.BankAccountData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.domain.CurrencyRepository;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.SupplierRepository;
import io.smarthealth.supplier.domain.specification.SupplierSpecification;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final PaymentTermsRepository paymentTermsRepository;
    private final CurrencyRepository currencyRepository;
    private final PricebookService pricebookService;
    private final AdminService adminService;

    public SupplierService(SupplierRepository supplierRepository, PaymentTermsRepository paymentTermsRepository, CurrencyRepository currencyRepository, PricebookService pricebookService, AdminService adminService) {
        this.supplierRepository = supplierRepository;
        this.paymentTermsRepository = paymentTermsRepository;
        this.currencyRepository = currencyRepository;
        this.pricebookService = pricebookService;
        this.adminService = adminService;
    }

     

    public SupplierData createSupplier(SupplierData supplierData) {
        Supplier supplier = new Supplier();
        if (supplierData.getType() != null) {
            supplier.setSupplierType(Supplier.Type.valueOf(supplierData.getType()));
        }

        supplier.setSupplierName(supplierData.getSupplierName());
        supplier.setLegalName(supplierData.getLegalName());
        supplier.setTaxNumber(supplierData.getTaxNumber());
        supplier.setWebsite(supplierData.getWebsite());

        if (supplierData.getCurrency() != null) {
            Optional<Currency> c = currencyRepository.findById(supplierData.getCurrencyId());
            if (c.isPresent()) {
                supplier.setCurrency(c.get());
            }
        }
        if (supplierData.getPricebookId() != null) {
            Optional<PriceBook> book = pricebookService.getPricebook(supplierData.getPricebookId());
            if (book.isPresent()) {
                supplier.setPricelist(book.get());
            }
        }

        if (supplierData.getPaymentTermsId() != null) {
            Optional<PaymentTerms> terms = paymentTermsRepository.findById(supplierData.getPaymentTermsId());
            if (terms.isPresent()) {
                supplier.setPaymentTerms(terms.get());
            }
        }

        if (supplierData.getBank() != null) {
            supplier.setBankAccount(BankAccountData.map(supplierData.getBank()));
        }

        if (supplierData.getAddresses() != null) {

            List<Address> addresses = adminService.createAddresses(supplierData.getAddresses());
//                    supplierData.getAddresses()
//                    .stream()
//                    .map(adds -> AddressData.map(adds))
//                    .collect(Collectors.toList());
            //save the address
            supplier.setAddress(addresses);
        }

        if (supplierData.getContacts() != null) {

            List<Contact> contacts = adminService.createContacts(supplierData.getContacts());
//                    supplierData.getContacts()
//                    .stream()
//                    .map(contc -> ContactData.map(contc))
//                    .collect(Collectors.toList());
            supplier.setContacts(contacts);
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        return SupplierData.map(savedSupplier);
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public Optional<Supplier> getSupplierByName(String companyName, String legalName) {
        return supplierRepository.findBySupplierNameOrLegalName(companyName, legalName);
    }
    public Optional<Supplier> getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierNameOrLegalName(supplierName, supplierName);
    }

    public Page<Supplier> getSuppliers(String type, boolean includeClosed, String term, Pageable page) {
        Supplier.Type searchType = null;
        if (type != null && EnumUtils.isValidEnum(Supplier.Type.class, type)) {
            searchType = Supplier.Type.valueOf(type);
        }
        Specification<Supplier> spec = SupplierSpecification.createSpecification(searchType, term, includeClosed);
        return supplierRepository.findAll(spec, page);
    }
}

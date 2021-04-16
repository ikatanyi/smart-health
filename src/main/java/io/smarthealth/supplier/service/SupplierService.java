package io.smarthealth.supplier.service;

import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.pricelist.data.PriceBookData;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.administration.app.data.BankEmbeddedData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.administration.app.service.CurrencyService;
import io.smarthealth.administration.finances.service.PaymentTermsService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.data.SupplierStatement;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.SupplierMetadata;
import io.smarthealth.supplier.domain.SupplierRepository;
import io.smarthealth.supplier.domain.enumeration.SupplierType;
import io.smarthealth.supplier.domain.specification.SupplierSpecification;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final PaymentTermsService paymentService;
    private final CurrencyService currencyService;
    private final PricebookService pricebookService;
    private final AdminService adminService;
    private final AccountRepository accountRepository;

    public SupplierService(SupplierRepository supplierRepository, PaymentTermsService paymentService, CurrencyService currencyService, PricebookService pricebookService, AdminService adminService, AccountRepository accountRepository) {
        this.supplierRepository = supplierRepository;
        this.paymentService = paymentService;
        this.currencyService = currencyService;
        this.pricebookService = pricebookService;
        this.adminService = adminService;
        this.accountRepository = accountRepository;
    }

    public SupplierData createSupplier(SupplierData supplierData) {
        Supplier supplier = new Supplier();
        supplier.setActive(Boolean.TRUE);
        if (supplierData.getSupplierType() != null) {
            supplier.setSupplierType(supplierData.getSupplierType());
        }

        supplier.setSupplierName(supplierData.getSupplierName());
        supplier.setLegalName(supplierData.getLegalName());
        supplier.setTaxNumber(supplierData.getTaxNumber());
        supplier.setWebsite(supplierData.getWebsite());

        if (supplierData.getCurrencyId() != null) {
            Optional<Currency> c = currencyService.getCurrency(supplierData.getCurrencyId());
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
            Optional<PaymentTerms> terms = paymentService.getPaymentTerm(supplierData.getPaymentTermsId());
            if (terms.isPresent()) {
                supplier.setPaymentTerms(terms.get());
            }
        }

        if (supplierData.getBank() != null) {
            supplier.setBankAccount(BankEmbeddedData.map(supplierData.getBank()));
        }

        if (supplierData.getAddresses() != null && supplierData.getAddresses().getPhone() != null) {
            Address addresses = adminService.createAddress(supplierData.getAddresses());
            addresses.setType(Address.Type.Office);
            supplier.setAddress(addresses);
        }

        if (supplierData.getContact() != null) {
            Contact contact = adminService.createContact(supplierData.getContact());
            supplier.setContact(contact);
        }

        if (supplierData.getCreditAccountNo() != null) {
            Account acc = accountRepository.findByIdentifier(supplierData.getCreditAccountNo()).get();
            supplier.setCreditAccount(acc);
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        return savedSupplier.toData();
    }

    public Supplier getSupplierOrThrow(Long id) {
        Supplier supplier = getSupplierById(id)
                .orElseThrow(() -> APIException.notFound("Supplier with id {0} not found.", id));
        return supplier;
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

    public Page<Supplier> getSuppliers(String type, Boolean includeClosed, String term, Pageable page) {
        SupplierType searchType = null;
        if (type != null && EnumUtils.isValidEnum(SupplierType.class, type)) {
            searchType = SupplierType.valueOf(type);
        }
        Specification<Supplier> spec = SupplierSpecification.createSpecification(searchType, term, includeClosed);
        return supplierRepository.findAll(spec, page);
    }

    public SupplierMetadata getSupplierMetadata() {
        SupplierMetadata metadata = new SupplierMetadata();
        List<Currency> currency = currencyService.getCurrencies();
        List<PaymentTerms> paymentTerms = paymentService.getPaymentTerms();
        List<PriceBookData> pricebooks = pricebookService.getPricebooks();
        List<Account> accounts = accountRepository.findByType(AccountType.LIABILITY);
        if (!accounts.isEmpty()) {
            List<SimpleAccountData> acc = accounts
                    .stream()
                    .map(x -> SimpleAccountData.map(x)).collect(Collectors.toList());
            metadata.setAccounts(acc);
        }
        metadata.setCurrencies(currency);
        metadata.setPaymentTerms(paymentTerms);
        metadata.setPricelists(pricebooks);

        return metadata;
    }
    public List<SupplierStatement> getStatement(Long supplierId, DateRange range){
        List<SupplierStatement> lists= supplierRepository.getSupplierStatement(supplierId);
        return lists;
    }
}

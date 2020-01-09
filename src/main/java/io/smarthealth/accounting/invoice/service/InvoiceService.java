package io.smarthealth.accounting.invoice.service;

import io.smarthealth.accounting.billing.domain.BillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.CreateInvoiceData;
import io.smarthealth.accounting.invoice.data.CreateInvoiceItemData;
import io.smarthealth.accounting.invoice.data.DebtorData;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.domain.Debtors;
import io.smarthealth.accounting.invoice.domain.DebtorsRepository;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.service.TxnService;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final DebtorsRepository debtorRepository;
//    private final ItemService itemService;
    private final BillingService billingService;
    private final PayerService payerService;
    private final SchemeService schemeService;
    private final TxnService txnService;

    @Transactional
    public String createInvoice(CreateInvoiceData invoiceData) {

        final String trxId = txnService.nextId();// UUID.randomUUID().toString();
        final String invoiceNo = RandomStringUtils.randomNumeric(5);

        invoiceData.getPayers()
                .stream()
                .forEach(debt -> {

                    Invoice invoice = new Invoice();
                    invoice.setDate(invoiceData.getDate());
                    invoice.setNotes(invoice.getNotes());
                    invoice.setNumber(invoiceNo);
                    invoice.setSubtotal(invoiceData.getSubTotal());
                    invoice.setDisounts(invoiceData.getDiscount());
                    invoice.setTaxes(invoiceData.getTaxes());
                    invoice.setTotal(invoiceData.getTotal());

                    if (!invoiceData.getItems().isEmpty()) {
                        List<InvoiceLineItem> items = invoiceData.getItems()
                                .stream()
                                .map(inv -> createItem(trxId, inv))
                                .collect(Collectors.toList());

                        invoice.addItems(items);
                    }
                    //

                    Invoice inv = invoiceRepository.save(invoice);

                    Payer payer = payerService.findPayerByIdWithNotFoundDetection(debt.getPayerId());
                    Scheme scheme = null;

                    Debtors debtor = createDebtor(debt);
                    debtor.setPayer(payer);
                    debtor.setScheme(scheme);
                    debtor.setInvoiceDate(inv.getDate());
                    debtor.setBillNumber(invoiceData.getBillNumber());
                    debtor.setPatientNumber(invoiceData.getPatientNumber());

                    debtorRepository.save(debtor);
                }
                );
        return trxId;

    }

    private InvoiceLineItem createItem(String trxId, CreateInvoiceItemData data) {
        InvoiceLineItem lineItem = new InvoiceLineItem();
        BillItem item = billingService.findBillItemById(data.getBillItemId());
        item.setStatus(BillStatus.Final);
  
        lineItem.setBillItem(item);
        lineItem.setDeleted(false);
        
        lineItem.setTransactionId(trxId);
        return lineItem;
    }

    private Debtors createDebtor(DebtorData debt) {
        Debtors debtor = new Debtors();
        debtor.setBalance(debt.getAmount()); 
        debtor.setInvoiceAmount(debt.getAmount());
        debtor.setMemberName(debt.getMemberName());
        debtor.setMemberNumber(debt.getMemberNo());
        return debtor;
    }

    public Optional<Invoice> findById(final Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> findByInvoiceNumber(final String invoiceNo) {
        return invoiceRepository.findByNumber(invoiceNo);
    }

    public Page<Invoice> fetchInvoices(String customer, String invoice, String receipt, Pageable pageable) {
        Specification<Invoice> spec = InvoiceSpecification.createSpecification(customer, invoice);
        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);
        return invoices;
    }

    public InvoiceData updateInvoice(Long id, InvoiceData data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Invoice findInvoiceOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Invoice with id {0} not found.", id));
    }

    public String emailInvoice(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public InvoiceData invoiceToEDI(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

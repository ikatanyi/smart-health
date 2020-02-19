package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PurchaseInvoiceService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SupplierService supplierService;
    private final JournalService journalService;

    public PurchaseInvoiceService(PurchaseInvoiceRepository purchaseInvoiceRepository, SupplierService supplierService, JournalService journalService) {
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierService = supplierService;
        this.journalService = journalService;
    }

    public PurchaseInvoiceData createPurchaseInvoice(PurchaseInvoiceData invoiceData) {
        PurchaseInvoice invoice = new PurchaseInvoice();

        Supplier supplier = supplierService.findOneWithNoFoundDetection(invoiceData.getSupplierId());
        invoice.setSupplier(supplier);
        invoice.setPurchaseOrderNumber(invoiceData.getPurchaseOrderNumber());
        invoice.setInvoiceDate(invoiceData.getInvoiceDate());
        invoice.setSerialNumber(invoiceData.getSerialNumber());
        invoice.setTransactionDate(invoiceData.getTransactionDate());
        invoice.setDueDate(invoiceData.getDueDate());
        invoice.setPaid(false);
        invoice.setIsReturn(false);
        invoice.setInvoiceNumber(invoiceData.getInvoiceNo());
        invoice.setInvoiceAmount(invoiceData.getInvoiceAmount());
        invoice.setInvoiceBalance(invoiceData.getInvoiceBalance());

        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        //then we need to save this
        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);

        return PurchaseInvoiceData.map(savedInvoice);
    }
  
    public void createPurchaseInvoice(Store store, SupplierStockEntry stockEntry) {
        PurchaseInvoice invoice = new PurchaseInvoice();
        Supplier supplier = supplierService.findOneWithNoFoundDetection(stockEntry.getSupplierId());
        invoice.setSupplier(supplier);
        invoice.setPurchaseOrderNumber(stockEntry.getOrderNumber());
        invoice.setInvoiceDate(stockEntry.getSupplierInvoiceDate());
        invoice.setTransactionDate(stockEntry.getTransactionDate());
        invoice.setDueDate(stockEntry.getSupplierInvoiceDueDate());
        invoice.setPaid(false);
        invoice.setIsReturn(false);
        invoice.setInvoiceNumber(stockEntry.getSupplierInvoiceNumber());
        invoice.setDiscount(stockEntry.getDiscount());
        invoice.setTax(stockEntry.getTaxes());
        invoice.setInvoiceAmount(stockEntry.getAmount());
        invoice.setInvoiceBalance(stockEntry.getNetAmount());
        invoice.setNetAmount(stockEntry.getNetAmount());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        invoice.setTransactionNumber(stockEntry.getTransactionId());

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);
        journalService.save(toJournal(savedInvoice, store));
//        journalEntryService.createJournalEntry(store, savedInvoice);
    }

    public Optional<PurchaseInvoice> findByInvoiceNumber(final String orderNo) {
        return purchaseInvoiceRepository.findByInvoiceNumber(orderNo);
    }

    public PurchaseInvoice findOneWithNoFoundDetection(Long id) {
        return purchaseInvoiceRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Invoice with Id {0} not found", id));
    }

    public Page<PurchaseInvoice> getPurchaseInvoices(PurchaseInvoiceStatus status, Pageable page) {
        if (status != null) {
            return purchaseInvoiceRepository.findByStatus(status, page);
        }
        return purchaseInvoiceRepository.findAll(page);
    }

    private JournalEntry toJournal(PurchaseInvoice invoice, Store store) {
        if (invoice.getSupplier().getCreditAccount() == null) {
            throw APIException.badRequest("Supplier Ledger Account Not Mapped for {0} ", invoice.getSupplier().getSupplierName());
        }
        if (store.getInventoryAccount() == null) {
            throw APIException.badRequest("Inventory Asset Ledger Account Not Mapped for {0} ", store.getStoreName());
        }

        String creditAcc = invoice.getSupplier().getCreditAccount().getIdentifier();
        String debitAcc = store.getInventoryAccount().getIdentifier();
        BigDecimal amount = invoice.getNetAmount();
        JournalEntry toSave = new JournalEntry(invoice.getInvoiceDate(), "Purchase Invoice - " + invoice.getInvoiceNumber(),
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAcc, JournalEntryItem.Type.DEBIT, amount),
                    new JournalEntryItem(creditAcc, JournalEntryItem.Type.CREDIT, amount)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionNumber());
        toSave.setTransactionType(TransactionType.Purchase);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
}

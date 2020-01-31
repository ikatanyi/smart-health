package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
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
    private final JournalEntryService journalEntryService;

    public PurchaseInvoiceService(PurchaseInvoiceRepository purchaseInvoiceRepository, SupplierService supplierService, JournalEntryService journalEntryService) {
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierService = supplierService;
        this.journalEntryService = journalEntryService;
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

        journalEntryService.createJournalEntry(store, savedInvoice);
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
}

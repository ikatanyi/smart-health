package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.service.InventoryEventSender;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteData;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteItem;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseCreditNote;
import io.smarthealth.stock.purchase.domain.PurchaseCreditNoteRepository;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.domain.specification.PurchaseInvoiceSpecification;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreRepository;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class PurchaseInvoiceService {

    private final SequenceNumberService sequenceNumberService;
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SupplierService supplierService;
    private final JournalService journalService;
    private final ItemService itemService;
    private final StoreRepository storeRepository;
    private final StockEntryRepository stockEntryRepository;
    private final InventoryEventSender inventoryEventSender;

    public PurchaseInvoice createPurchaseInvoice(PurchaseInvoiceData invoiceData) {
        Supplier supplier = supplierService.getSupplierOrThrow(invoiceData.getSupplierId());
        String trdId = invoiceData.getTransactionId() == null ? sequenceNumberService.next(1L, Sequences.Transactions.name()) : invoiceData.getTransactionId();

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setSupplier(supplier);
        invoice.setPurchaseOrderNumber(invoiceData.getPurchaseOrderNumber());
        invoice.setInvoiceDate(invoiceData.getInvoiceDate());
        invoice.setTransactionDate(invoiceData.getTransactionDate());
        invoice.setTransactionNumber(trdId);
        invoice.setDueDate(invoiceData.getDueDate());
        invoice.setPaid(false);
        invoice.setIsReturn(false);
        invoice.setInvoiceNumber(invoiceData.getInvoiceNo());
        invoice.setInvoiceAmount(invoiceData.getInvoiceAmount());
        invoice.setInvoiceBalance(invoiceData.getInvoiceBalance());

        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        //then we need to save this
        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);

        return savedInvoice;
    }

    public void createPurchaseInvoice(Store store, SupplierStockEntry stockEntry) {
        Supplier supplier = supplierService.getSupplierOrThrow(stockEntry.getSupplierId());

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setSupplier(supplier);
        invoice.setType(PurchaseInvoice.Type.Stock_Delivery);
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
        invoice.setInvoiceBalance(stockEntry.getAmount());
        invoice.setNetAmount(stockEntry.getNetAmount());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        invoice.setTransactionNumber(stockEntry.getTransactionId());

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);
        journalService.save(toJournal(savedInvoice, store));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PurchaseInvoice doCreditNote(PurchaseCreditNoteData creditNote) {

        Supplier supplier = supplierService.getSupplierOrThrow(creditNote.getSupplierId());
        Store store = getStore(creditNote.getStoreId());
        String trdId = creditNote.getTransactionId() == null ? sequenceNumberService.next(1L, Sequences.Transactions.name()) : creditNote.getTransactionId();

        PurchaseInvoice invoice = new PurchaseInvoice();

        invoice.setSupplier(supplier);
        invoice.setType(PurchaseInvoice.Type.Stock_Returns);
        invoice.setPurchaseOrderNumber(creditNote.getCreditNoteNumber());
        invoice.setInvoiceDate(creditNote.getCreditDate());
        invoice.setTransactionDate(creditNote.getCreditDate());
        invoice.setDueDate(creditNote.getCreditDate());
        invoice.setPaid(false);
        invoice.setIsReturn(true);
        invoice.setInvoiceNumber(creditNote.getInvoiceNumber());
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setTax(BigDecimal.ZERO);
        invoice.setInvoiceAmount(creditNote.getAmount().negate());
        invoice.setInvoiceBalance(creditNote.getAmount().negate());
        invoice.setNetAmount(creditNote.getAmount().negate());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        invoice.setTransactionNumber(trdId);

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);

        if (!creditNote.getItems().isEmpty()) {
            doStockReturns(store, creditNote);
        }
        toJournal(invoice, store);
        return savedInvoice;
    }

    public Optional<PurchaseInvoice> findByInvoiceNumber(final String orderNo) {
        return purchaseInvoiceRepository.findByInvoiceNumber(orderNo);
    }

    public PurchaseInvoice findOneWithNoFoundDetection(Long id) {
        return purchaseInvoiceRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Invoice with Id {0} not found", id));
    }

    public Page<PurchaseInvoice> getSupplierInvoices(Long supplierId, String invoiceNumber, Boolean paid, PurchaseInvoiceStatus status, Pageable page) {
        Specification<PurchaseInvoice> specs = PurchaseInvoiceSpecification.createSpecification(supplierId, invoiceNumber, paid, status);
        return purchaseInvoiceRepository.findAll(specs, page);

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

        BigDecimal amount = invoice.getNetAmount();
        JournalEntry toSave;
        if (invoice.getType() == PurchaseInvoice.Type.Stock_Delivery) {
            String narration = "Stocks delivery for the invoice " + invoice.getInvoiceNumber();
            toSave = new JournalEntry(invoice.getInvoiceDate(), "Purchase Invoice - " + invoice.getInvoiceNumber(),
                    new JournalEntryItem[]{
                        new JournalEntryItem(store.getInventoryAccount(), narration, amount, BigDecimal.ZERO),
                        new JournalEntryItem(invoice.getSupplier().getCreditAccount(), narration, BigDecimal.ZERO, amount)
                    }
            );
            toSave.setTransactionType(TransactionType.Purchase);
        } else {
            String narration = "Credit Note  " + invoice.getPurchaseOrderNumber() + " for the Invoice No. " + invoice.getInvoiceNumber();
            toSave = new JournalEntry(invoice.getInvoiceDate(), "Credit Note  " + invoice.getPurchaseOrderNumber() + " for Amount " + SystemUtils.formatCurrency(invoice.getInvoiceAmount()) + " in reference to " + invoice.getInvoiceNumber(),
                    new JournalEntryItem[]{
                        new JournalEntryItem(invoice.getSupplier().getCreditAccount(), narration, amount, BigDecimal.ZERO),
                        new JournalEntryItem(store.getInventoryAccount(), narration, BigDecimal.ZERO, amount)
                    }
            );
            toSave.setTransactionType(TransactionType.Purchase);
        }

        toSave.setTransactionNo(invoice.getTransactionNumber());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

    private void doStockReturns(Store store, PurchaseCreditNoteData creditNote) {
        creditNote.getItems().stream()
                .forEach(st -> {
                    Item item = itemService.findItemEntityOrThrow(st.getItemId());

                    BigDecimal qty = BigDecimal.valueOf(st.getQuantity());

                    StockEntry stock = new StockEntry();
                    stock.setAmount(st.getAmount());
                    stock.setDeliveryNumber(creditNote.getCreditNoteNumber());
                    stock.setQuantity(qty.doubleValue());
                    stock.setItem(item);
                    stock.setMoveType(MovementType.Purchase);
                    stock.setPrice(st.getRate());
                    stock.setPurpose(MovementPurpose.Returns);
                    stock.setReferenceNumber(creditNote.getCreditNoteNumber());
                    stock.setStore(store);
                    stock.setTransactionDate(LocalDate.now());
                    stock.setTransactionNumber(creditNote.getTransactionId());

                    stockEntryRepository.save(stock);
                    inventoryEventSender.process(new InventoryEvent(InventoryEvent.Type.Decrease, store, item, qty.doubleValue()));
                });
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id).orElseThrow(() -> APIException.notFound("Store with id {0} not found", id));
    }

}

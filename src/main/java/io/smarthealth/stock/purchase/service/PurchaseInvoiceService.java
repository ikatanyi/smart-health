package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.*;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.SystemUtils;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.events.InventorySpringEventPublisher;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
//    private final InventoryEventSender inventoryEventSender;
    private final InventorySpringEventPublisher inventoryEventSender;
    private final PurchaseCreditNoteRepository purchaseCreditNoteRepository;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<PurchaseInvoice> createPurchaseInvoice(SupplierBill invoiceData) {

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String docNo = sequenceNumberService.next(1L, Sequences.PurchaseInvoiceNumber.name());

        List<PurchaseInvoice> savedInvoices = new ArrayList<>();
        invoiceData.getBills().stream()
                .forEach(
                        bill -> {
                            Supplier supplier = supplierService.getSupplierOrThrow(invoiceData.getSupplierId());
                            PurchaseInvoice invoice = new PurchaseInvoice();
                            invoice.setSupplier(supplier);
                            invoice.setPurchaseOrderNumber(bill.getReference());
                            invoice.setInvoiceDate(bill.getInvoiceDate());
                            invoice.setTransactionDate(LocalDate.now());
                            invoice.setTransactionNumber(trdId);
                            invoice.setType(PurchaseInvoice.Type.Supplier_Bill);
                            invoice.setDueDate(bill.getDueDate());
                            invoice.setPaid(false);
                            invoice.setIsReturn(false);
                            invoice.setApproved(false);
                            invoice.setInvoiceNumber(bill.getInvoiceNumber());
                            invoice.setInvoiceAmount(bill.getInvoiceAmount());
                            invoice.setNetAmount(bill.getNetAmount());
                            invoice.setDiscount(bill.getDiscountAmount());
                            invoice.setTax(bill.getTaxAmount());
                            invoice.setInvoiceBalance(bill.getNetAmount());
                            invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
                            invoice.setDocumentNumber(docNo);

                            bill.setTransactionId(trdId);

                            PurchaseInvoice savedInv = purchaseInvoiceRepository.save(invoice);
                            journalService.save(toJournal(supplier, bill));
                            savedInvoices.add(savedInv);

                        }
                );
        return savedInvoices;
    }

    public void createPurchaseInvoice(Store store, SupplierStockEntry stockEntry) {
        Supplier supplier = supplierService.getSupplierOrThrow(stockEntry.getSupplierId());
        String docNo = stockEntry.getDocumentNo()!=null ? stockEntry.getDocumentNo() : sequenceNumberService.next(1L, Sequences.StockReturnNumber.name());
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
        invoice.setDiscount(stockEntry.getDiscountTotals());
        invoice.setTax(stockEntry.getTaxTotals());
        invoice.setInvoiceAmount(stockEntry.getAmount());
        invoice.setInvoiceBalance(stockEntry.getNetAmount());
        invoice.setNetAmount(stockEntry.getNetAmount());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        invoice.setTransactionNumber(stockEntry.getTransactionId());
        invoice.setDocumentNumber(docNo);

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);
        journalService.save(toJournal(savedInvoice, store));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PurchaseInvoice doCreditNote(PurchaseCreditNoteData creditNote) {

        Supplier supplier = supplierService.getSupplierOrThrow(creditNote.getSupplierId());
        String docNo = sequenceNumberService.next(1L, Sequences.StockReturnNumber.name());

        String trdId = creditNote.getTransactionId() == null ? sequenceNumberService.next(1L, Sequences.Transactions.name()) : creditNote.getTransactionId();

        creditNote.setDocumentNumber(docNo);
        creditNote.setTransactionId(trdId);

        PurchaseInvoice invoice = new PurchaseInvoice();

        invoice.setSupplier(supplier);
        invoice.setType(PurchaseInvoice.Type.Stock_Returns);
        invoice.setPurchaseOrderNumber(creditNote.getSupplierReference());
        invoice.setInvoiceDate(creditNote.getCreditDate());
        invoice.setTransactionDate(creditNote.getCreditDate());
        invoice.setDueDate(creditNote.getCreditDate());
        invoice.setPaid(false);
        invoice.setIsReturn(true);
        invoice.setInvoiceNumber(creditNote.getInvoiceNumber());
        invoice.setDiscount(creditNote.getTotalDiscount());
        invoice.setTax(creditNote.getTotalVat());
        invoice.setInvoiceAmount(creditNote.getTotalAmount().negate());
        invoice.setInvoiceBalance(creditNote.getTotalAmount().negate());
        invoice.setNetAmount(creditNote.getTotalAmount().negate());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        invoice.setTransactionNumber(trdId);
        invoice.setDocumentNumber(docNo);
        invoice.setPurchaseOrderNumber(creditNote.getInvoiceDocumentNo());

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);
        //reduce the invoice with the amount
        if(creditNote.getInvoiceDocumentNo()!=null) {
            Optional<PurchaseInvoice> inv = purchaseInvoiceRepository.findByDocumentNumber(creditNote.getInvoiceDocumentNo());
            //creditNote.getTotalAmount()}
            if(inv.isPresent()){
                //TODO reduce the balance and not the invoice amount
                PurchaseInvoice pinv = inv.get();
                BigDecimal bal = pinv.getInvoiceBalance().subtract(creditNote.getTotalAmount());
                pinv.setInvoiceBalance(bal);
                purchaseInvoiceRepository.save(pinv);
            }
        }

        if (!creditNote.getItems().isEmpty()) {
            doStockReturns(creditNote);
        }
        Optional<PurchaseCreditNoteItemData> pcid = creditNote.getItems().stream().findFirst();
        if(pcid.isPresent()) {
            Store store = getStore(pcid.get().getStoreId());
            toJournal(invoice, store);
        }
        return savedInvoice;
    }

    public Optional<PurchaseInvoice> findByInvoiceNumber(final String orderNo) {
        return purchaseInvoiceRepository.findByInvoiceNumber(orderNo);
    }

    public PurchaseInvoice findOneWithNoFoundDetection(Long id) {
        return purchaseInvoiceRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Invoice with Id {0} not found", id));
    }
    public List<StockEntry> findPurchaseInvoiceItems(String referenceNumber, String docNumber){
        if(docNumber!=null){
            return stockEntryRepository.findStockEntriesByDeliveryNumber(docNumber);
        }
        return stockEntryRepository.findByMoveTypeAndReferenceNumber(MovementType.Purchase, referenceNumber);
    }
    public PurchaseCreditNote findByNumberWithNoFoundDetection(String creditNoteNumber) {
        return purchaseCreditNoteRepository.findByNumber(creditNoteNumber)
                .orElseThrow(() -> APIException.notFound("Purchase Invoice with creditNoteNumber {0} not found", creditNoteNumber));
    }

    public Page<PurchaseInvoice> getSupplierInvoices(Long supplierId, String invoiceNumber, Boolean paid, DateRange range, PurchaseInvoiceStatus status, Boolean approved, String query, List<PurchaseInvoice.Type> invoiceType, Pageable page) {
        Specification<PurchaseInvoice> specs = PurchaseInvoiceSpecification.createSpecification(supplierId, invoiceNumber, paid, range, status, approved, query,invoiceType);
        return purchaseInvoiceRepository.findAll(specs, page);

    }

    public Page<PurchaseInvoice> getPurchaseInvoices(PurchaseInvoiceStatus status, Pageable page) {
        if (status != null) {
            return purchaseInvoiceRepository.findByStatus(status, page);
        }
        return purchaseInvoiceRepository.findAll(page);
    }

    private JournalEntry toJournal(Supplier supplier, SupplierBillItem invoice) {
        if (supplier.getCreditAccount() == null) {
            throw APIException.badRequest("Supplier Ledger Account Not Mapped for {0} ", supplier.getSupplierName());
        }
        Optional<Account> debitAccount = accountRepository.findByIdentifier(invoice.getAccountIdentifier());

        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Expense Ledger with Identifier {0} Not Found ", invoice.getAccountIdentifier());
        }

        BigDecimal amount = invoice.getNetAmount();
        BigDecimal discount = invoice.getDiscountAmount();
        JournalEntry toSave;

        String narration = "Supplier bill " + invoice.getInvoiceNumber() + " received. " + invoice.getReference();
        String narration2 = "Stocks delivery Discount for the invoice " + invoice.getInvoiceNumber();
        List<JournalEntryItem> items = new ArrayList<>();

        items.add(new JournalEntryItem(debitAccount.get(), narration, amount, BigDecimal.ZERO));
        items.add(new JournalEntryItem(supplier.getCreditAccount(), narration, BigDecimal.ZERO, amount));

        if (discount != null && discount != BigDecimal.ZERO) {
            Optional<FinancialActivityAccount> discountAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Discount_Received);
            if (!discountAccount.isPresent()) {
                throw APIException.badRequest("Discount Received Ledger Not Mapped");
            }
            items.add(new JournalEntryItem(supplier.getCreditAccount(), narration2, discount, BigDecimal.ZERO));
            items.add(new JournalEntryItem(discountAccount.get().getAccount(), narration2, BigDecimal.ZERO, discount));
        }

        toSave = new JournalEntry(invoice.getInvoiceDate(), "Supplier Bill - " + invoice.getInvoiceNumber() + " received.", items);
        toSave.setTransactionType(TransactionType.Purchase);

        toSave.setTransactionNo(invoice.getTransactionId());
        toSave.setStatus(JournalState.PENDING);

        return toSave;
    }

    private void doStockReturns(PurchaseCreditNoteData creditNote) {
        creditNote.getItems().stream()
                .forEach(st -> {
                    Store store = getStore(st.getStoreId());
                    Item item = itemService.findItemEntityOrThrow(st.getItemId());

                    BigDecimal qty = st.getQuantity()!=null ? BigDecimal.valueOf(st.getQuantity()).negate() : BigDecimal.ONE.negate();

                    StockEntry stock = new StockEntry();
                    stock.setAmount(st.getRate().multiply(qty));
                    stock.setDeliveryNumber(creditNote.getDocumentNumber());
                    stock.setQuantity(qty.doubleValue());
                    stock.setItem(item);
                    stock.setMoveType(MovementType.Purchase);
                    stock.setPrice(st.getRate());
                    stock.setPurpose(MovementPurpose.Returns);
                    stock.setReferenceNumber(creditNote.getInvoiceNumber());
                    stock.setStore(store);
                    stock.setTransactionDate(LocalDate.now());
                    stock.setTransactionNumber(creditNote.getTransactionId());
                    stock.setDiscount(st.getDiscount());
                    stock.setTax(st.getTax());

                    stockEntryRepository.save(stock);
//                    inventoryEventSender.process(new InventoryEvent(InventoryEvent.Type.Decrease, store, item, qty.doubleValue()));
                     inventoryEventSender.publishInventoryEvent(InventoryEvent.Type.Decrease, store, item, qty.doubleValue());
                });
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id).orElseThrow(() -> APIException.notFound("Store with id {0} not found", id));
    }

    private JournalEntry toJournal(PurchaseInvoice invoice, Store store) {
        if (invoice.getSupplier().getCreditAccount() == null) {
            throw APIException.badRequest("Supplier Ledger Account Not Mapped for {0} ", invoice.getSupplier().getSupplierName());
        }
        if (store.getInventoryAccount() == null) {
            throw APIException.badRequest("Inventory Asset Ledger Account Not Mapped for {0} ", store.getStoreName());
        }

        BigDecimal amount = invoice.getNetAmount();
        BigDecimal discount = invoice.getDiscount();
        JournalEntry toSave;
        if (invoice.getType() == PurchaseInvoice.Type.Stock_Delivery) {

            String narration = "Stocks delivery for the invoice " + invoice.getInvoiceNumber();
            String narration2 = "Stocks delivery Discount for the invoice " + invoice.getInvoiceNumber();
            List<JournalEntryItem> items = new ArrayList<>();

            items.add(new JournalEntryItem(store.getInventoryAccount(), narration, amount, BigDecimal.ZERO));
            items.add(new JournalEntryItem(invoice.getSupplier().getCreditAccount(), narration, BigDecimal.ZERO, amount));

            if (invoice.getDiscount() != null && invoice.getDiscount() != BigDecimal.ZERO) {
                Optional<FinancialActivityAccount> discountAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Discount_Received);
                if (!discountAccount.isPresent()) {
                    throw APIException.badRequest("Discount Received Ledger Not Mapped");
                }
                items.add(new JournalEntryItem(invoice.getSupplier().getCreditAccount(), narration2, discount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(discountAccount.get().getAccount(), narration2, BigDecimal.ZERO, discount));
            }

            toSave = new JournalEntry(invoice.getInvoiceDate(), "Purchase Invoice - " + invoice.getInvoiceNumber(), items);
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

    @Transactional
    public List<PurchaseInvoice> approveInvoice(List<ApproveSupplierBill> billsToApprove) {

        List<PurchaseInvoice> lists = billsToApprove.stream()
                .map(
                        bill -> {
                            Optional<PurchaseInvoice> inv = purchaseInvoiceRepository.findById(bill.getBillId());
                            if (inv.isPresent()) {
                                PurchaseInvoice purchaseInvoice = inv.get();
                                purchaseInvoice.setApproved(true);
                                purchaseInvoice.setApprovalDate(LocalDate.now());
                                purchaseInvoice.setApprovedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                                return purchaseInvoice;
                            }
                            return null;
                        }
                )
                .filter(x -> x != null)
                .collect(Collectors.toList());

        return purchaseInvoiceRepository.saveAll(lists);

    }

    public SupplierInvoiceReport getSupplierInvoiceReport(String documentNo){
        Optional<PurchaseInvoice> invoice = purchaseInvoiceRepository.findByDocumentNumber(documentNo);

        List<StockEntry> stockEntries = stockEntryRepository.findStockEntriesByDeliveryNumber(documentNo);
        if(invoice.isPresent()){
            return new SupplierInvoiceReport(invoice.get(),stockEntries);
        }
        return new SupplierInvoiceReport();
    }
}

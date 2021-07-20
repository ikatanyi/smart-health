package io.smarthealth.stock.purchase.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.purchase.data.ApproveSupplierBill;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteData;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.data.SupplierBill;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Api
@RestController
@RequestMapping("/api")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService service;

    public PurchaseInvoiceController(PurchaseInvoiceService service) {
        this.service = service;
    }

    @PostMapping("/purchaseinvoices")
    @PreAuthorize("hasAuthority('create_purchaseinvoices')")
    public ResponseEntity<?> createPurchaseInvoice(@Valid @RequestBody SupplierBill orderData) {

        List<PurchaseInvoice> list = service.createPurchaseInvoice(orderData);

        Pager<List<PurchaseInvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Invoice created successful");
        pagers.setContent(
                list.stream()
                        .map(x -> x.toData())
                        .collect(Collectors.toList())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PostMapping("/purchaseinvoices/approve")
    @PreAuthorize("hasAuthority('create_purchaseinvoices')")
    public ResponseEntity<?> approveInvoices(@Valid @RequestBody List<ApproveSupplierBill> approveBill) {
        List<PurchaseInvoice> list = service.approveInvoice(approveBill);

        Pager<List<PurchaseInvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Invoice approved successful");
        pagers.setContent(
                list.stream()
                        .map(x -> x.toData())
                        .collect(Collectors.toList())
        );

        return ResponseEntity.status(HttpStatus.OK).body(pagers);

    }

    @GetMapping("/purchaseinvoices/{id}")
    @PreAuthorize("hasAuthority('view_purchaseinvoices')")
    public PurchaseInvoiceData  getPurchaseInvoice(@PathVariable(value = "id") Long code) {
        PurchaseInvoice po = service.findOneWithNoFoundDetection(code);
        return po.toData();
    }

    @GetMapping("/purchaseinvoices/items")
    public ResponseEntity<List<StockEntryData>> getPurchaseInvoiceItems(
            @RequestParam(value = "invoiceNo", required = false) String invoiceNumber,
            @RequestParam(value = "docNo", required = false) String docNo) {

        List<StockEntryData> entries = service.findPurchaseInvoiceItems(invoiceNumber, docNo)
                .stream().map(StockEntry::toData).collect(Collectors.toList());

        return ResponseEntity.ok(entries);
    }

    @GetMapping("/purchaseinvoices")
    @PreAuthorize("hasAuthority('view_purchaseinvoices')")
    public ResponseEntity<Pager<List<PurchaseInvoiceData>>> getAllPurchaseInvoices(
            @RequestParam(value = "supplier_id", required = false) Long supplierId,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "approved", required = false) Boolean approved,
            @RequestParam(value = "invoice_no", required = false) String invoiceNumber,
            @RequestParam(value = "status", required = false) final PurchaseInvoiceStatus status,
            @RequestParam(value = "invoiceType", required = false) final List<PurchaseInvoice.Type> invoiceType,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size, Sort.by("id").descending());
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<PurchaseInvoiceData> list = service.getSupplierInvoices(supplierId, invoiceNumber, paid, range, status, approved,query,invoiceType, pageable) // service.getPurchaseInvoices(status, pageable)
                .map(u -> u.toData());

        Pager<List<PurchaseInvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Purchase Orders");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/purchaseinvoices/credit-note")
    @PreAuthorize("hasAuthority('create_purchaseinvoices')")
    public ResponseEntity<?> createCreditNote(@Valid @RequestBody PurchaseCreditNoteData data) {
        PurchaseInvoice result = service.doCreditNote(data);

        Pager<PurchaseInvoiceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Credit Note created successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
}

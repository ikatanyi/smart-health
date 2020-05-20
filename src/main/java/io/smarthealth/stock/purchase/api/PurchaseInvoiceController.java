package io.smarthealth.stock.purchase.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteData;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createPurchaseInvoice(@Valid @RequestBody PurchaseInvoiceData orderData) {

        PurchaseInvoice result = service.createPurchaseInvoice(orderData);

        Pager<PurchaseInvoiceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Invoice created successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/purchaseinvoices/{id}")
    @PreAuthorize("hasAuthority('view_purchaseinvoices')")
    public PurchaseInvoiceData getPurchaseInvoice(@PathVariable(value = "id") Long code) {
        PurchaseInvoice po = service.findOneWithNoFoundDetection(code);
        return po.toData();
    }

    @GetMapping("/purchaseinvoices")
    @PreAuthorize("hasAuthority('view_purchaseinvoices')")
    public ResponseEntity<?> getAllPurchaseInvoices(
            @RequestParam(value = "supplier_id", required = false) Long supplierId,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "invoice_no", required = false) String invoiceNumber,
            @RequestParam(value = "status", required = false) final PurchaseInvoiceStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PurchaseInvoiceData> list = service.getSupplierInvoices(supplierId, invoiceNumber, paid, status, pageable) // service.getPurchaseInvoices(status, pageable)
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

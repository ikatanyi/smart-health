package io.smarthealth.accounting.invoice.api;

import io.smarthealth.accounting.invoice.data.CreateInvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.TransData;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping("/invoices")
    public ResponseEntity<?> createInvoice(@Valid @RequestBody CreateInvoiceData invoiceData) {

        String trans = service.createInvoice(invoiceData);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Invoice successfully Created.");
        pagers.setContent(new TransData(trans));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/invoices/{id}")
    public InvoiceData getInvoice(@PathVariable(value = "id") Long id) {
        Invoice trans = service.findInvoiceOrThrowException(id);
        return InvoiceData.map(trans);
    }

    @PatchMapping("/invoices/{id}")
    public InvoiceData updateInvoice(@PathVariable(value = "id") Long id, InvoiceData transactionData) {
        InvoiceData trans = InvoiceData.map(service.updateInvoice(id, transactionData));
        return trans;
    }

    @PatchMapping("/invoices/{id}/verify")
    public InvoiceData updateInvoice(@PathVariable(value = "id") Long id, Boolean Isverified) {
        InvoiceData trans = InvoiceData.map(service.verifyInvoice(id, Isverified));
        return trans;
    }

    @GetMapping("/invoices")
    public ResponseEntity<?> getInvoices(
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "scheme", required = false) Long scheme,
            @RequestParam(value = "number", required = false) String invoice,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "status", required = false) InvoiceStatus status,
            @RequestParam(value = "filterPastDue", required = false, defaultValue = "false") boolean filterPastDue,
            @RequestParam(value = "amountGreaterThan", required = false, defaultValue = "0.00") double amountGreaterThan,
            @RequestParam(value = "amountLessThanOrEqualTo", required = false, defaultValue = "0.00") double amountLessThanOrEqualTo,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<InvoiceData> list = service.fetchInvoices(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo, pageable)
                .map(bill -> InvoiceData.map(bill));

        Pager<List<InvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Invoices");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/invoices/{id}/emails")
    public String sendReceipt(@PathVariable(value = "id") Long id) {
        return service.emailInvoice(id);
    }

    @PostMapping("/invoices/{id}/edi")
    public ResponseEntity<?> sendInvoiceToEDI(@PathVariable(value = "id") Long id) {
        InvoiceData trans = service.invoiceToEDI(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(trans);
    }
    //TODO
    /*
      Provide mpesa integrations, credit cards,
     */
}

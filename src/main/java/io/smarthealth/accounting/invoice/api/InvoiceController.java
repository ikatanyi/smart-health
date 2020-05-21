package io.smarthealth.accounting.invoice.api;

import io.smarthealth.accounting.invoice.data.CreateInvoice;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
@Api
@RestController
@RequestMapping("/api/")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAuthority('create_invoices')") 
    public ResponseEntity<?> createInvoice(@Valid @RequestBody CreateInvoice invoiceData) {

        List<InvoiceData> trans = service.createInvoice(invoiceData).stream().map(x -> x.toData()).collect(Collectors.toList());

        Pager<List<InvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Invoice (s) successfully Created.");
        pagers.setContent(trans);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAuthority('view_invoices')") 
    public ResponseEntity<?> getInvoice(@PathVariable(value = "id") Long id) {
        Invoice trans = service.getInvoiceByIdOrThrow(id);
        return ResponseEntity.ok(trans.toData());
    }

//    @PatchMapping("/invoices/{id}")
//    public InvoiceData updateInvoice(@PathVariable(value = "id") Long id, InvoiceData transactionData) {
//        InvoiceData trans = InvoiceData.map(service.updateInvoice(id, transactionData));
//        return trans;
//    }
//    @PatchMapping("/invoices/{id}/verify")
//    public InvoiceData updateInvoice(@PathVariable(value = "id") Long id, Boolean Isverified) {
//        InvoiceData trans = InvoiceData.map(service.verifyInvoice(id, Isverified));
//        return trans;
//    }
    @GetMapping("/invoices")
    @PreAuthorize("hasAuthority('view_invoices')") 
    public ResponseEntity<?> getInvoices(
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "scheme", required = false) Long scheme,
            @RequestParam(value = "number", required = false) String invoice,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "status", required = false) InvoiceStatus status,
            @RequestParam(value = "filterPastDue", required = false) Boolean filterPastDue,        
            @RequestParam(value = "amountGreaterThan", required = false) Double amountGreaterThan,
            @RequestParam(value = "amountLessThanOrEqualTo", required = false) Double amountLessThanOrEqualTo,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<InvoiceData> list = service.fetchInvoices(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo, pageable)
                .map(x -> x.toData());

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
    @PreAuthorize("hasAuthority('send_invoices')") 
    public String sendReceipt(@PathVariable(value = "id") Long id) {
        return service.emailInvoice(id);
    }

    @PostMapping("/invoices/{id}/edi")
    @PreAuthorize("hasAuthority('create_invoices')") 
    public ResponseEntity<?> sendInvoiceToEDI(@PathVariable(value = "id") Long id) {
        InvoiceData trans = service.invoiceToEDI(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(trans);
    }
    //TODO
    /*
      Provide mpesa integrations, credit cards,
     */
}

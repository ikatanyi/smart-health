package io.smarthealth.accounting.invoice.api;

import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.invoice.data.CreateInvoice;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceEditData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.data.MergeInvoice;
import io.smarthealth.accounting.invoice.data.statement.InterimInvoice;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.accounting.invoice.data.RebateInvoice;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.report.domain.enumeration.ReportName;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
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
    private final AuditTrailService auditTrailService;

    public InvoiceController(InvoiceService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAuthority('create_invoices')")
    public ResponseEntity<Pager<List<InvoiceData>>> createInvoice(@Valid @RequestBody CreateInvoice invoiceData) {

        List<InvoiceData> trans = service.createInvoice(invoiceData).stream().map(x -> x.toData()).collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Patient Invoice", "Created Patient invoice with invoiceNo " + trans.get(0).getNumber());
        Pager<List<InvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Invoice (s) successfully Created.");
        pagers.setContent(trans);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/invoices/rebate")
    public ResponseEntity<InvoiceData> createInvoiceRebate(@Valid @RequestBody RebateInvoice invoiceData) {
        Invoice invoice = service.createInvoiceRebate(invoiceData);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice.toData());
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAuthority('view_invoices')")
    public ResponseEntity<InvoiceData> getInvoice(@PathVariable(value = "id") Long id) {
        Invoice trans = service.getInvoiceByIdOrThrow(id);
        auditTrailService.saveAuditTrail("Patient Invoice", "Searched Invoice  identified by id " + id);
        return ResponseEntity.ok(trans.toData());
    }

    @PutMapping("/invoices/{id}")
    @PreAuthorize("hasAuthority('create_invoices')")
    public ResponseEntity<?> updateInvoice(@PathVariable(value = "id") Long id, @Valid @RequestBody InvoiceEditData invoiceData) {

        Invoice trans = service.updateInvoice(id, invoiceData);
        auditTrailService.saveAuditTrail("Patient Invoice", "Edited Invoice by id " + id);
        return ResponseEntity.ok(trans.toData());
    }

    @PostMapping("/invoices/{invoiceNumber}/add-items")
    @PreAuthorize("hasAuthority('create_invoices')")
    public ResponseEntity<InvoiceData> addInvoiceItem(@PathVariable(value = "invoiceNumber") String invoiceNumber, @Valid @RequestBody List<BillItemData> invoiceItems) {

        Invoice invoice = service.addInvoiceItem(invoiceNumber, invoiceItems);
        return ResponseEntity.ok(invoice.toData());
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAuthority('view_invoices')")
    public ResponseEntity<Pager<List<InvoiceData>>> getInvoices(
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "scheme", required = false) Long scheme,
            @RequestParam(value = "number", required = false) String invoice,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "status", required = false) InvoiceStatus status,
            @RequestParam(value = "filterPastDue", required = false) Boolean filterPastDue,
            @RequestParam(value = "awaitingSmart", required = false) Boolean awaitingSmart,
            @RequestParam(value = "amountGreaterThan", required = false) Double amountGreaterThan,
            @RequestParam(value = "amountLessThanOrEqualTo", required = false) Double amountLessThanOrEqualTo,
            @RequestParam(value = "hasCapitation", required = false) Boolean hasCapitation,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<InvoiceData> list = service.fetchInvoices(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, awaitingSmart, amountLessThanOrEqualTo, hasCapitation, pageable)
                .map(x -> x.toData());
        auditTrailService.saveAuditTrail("Patient Invoice", "Viewed Invoices");
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
        auditTrailService.saveAuditTrail("Patient Invoice", "Emailed Invoice identified by id " + id);
        return service.emailInvoice(id);
    }

    @PostMapping("/invoices/{id}/edi")
    @PreAuthorize("hasAuthority('create_invoices')")
    public ResponseEntity<?> sendInvoiceToEDI(@PathVariable(value = "id") Long id) {
        InvoiceData trans = service.invoiceToEDI(id);
        auditTrailService.saveAuditTrail("Patient Invoice", "Send Invoice identified by id " + id + " to EDI");
        return ResponseEntity.status(HttpStatus.CREATED).body(trans);
    }

    @PostMapping("/invoices/{invoiceNo}/void")
    public ResponseEntity<?> cancelInvoice(@PathVariable(value = "invoiceNo") String invoiceNo, @Valid @RequestBody List<InvoiceItemData> invoiceItems) {
        Invoice invoice = service.cancelInvoice(invoiceNo, invoiceItems);
        auditTrailService.saveAuditTrail("Patient Invoice", "Cancelled Invoice " + invoiceNo);
        return ResponseEntity.ok(invoice != null ? invoice.toData() : new InvoiceData());
    }

    @PutMapping("/invoices/{id}/update-smart-status")
    @PreAuthorize("hasAuthority('create_invoices')")
    public ResponseEntity<?> updateInvoiceSmartStatus(@PathVariable(value = "id") Long id, @RequestParam(value = "awaitingSmart", required = true) Boolean status) {

        service.updateInvoiceSmartStatus(id, status);
        auditTrailService.saveAuditTrail("Patient Invoice", "Edited Invoice identified by id " + id);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    //TODO
    /*
      Provide mpesa integrations, credit cards,
     */
    @PostMapping("/invoices/merge")
    public ResponseEntity<?> mergeInvoice(@Valid @RequestBody MergeInvoice mergeInvoice) {
        Invoice invoice = service.mergeInvoice(mergeInvoice);
        auditTrailService.saveAuditTrail("Patient Invoice", "merged Invoice" + mergeInvoice.getInvoiceNo() + " with " + mergeInvoice.getInvoiceToMerge());
        return ResponseEntity.ok(invoice != null ? invoice.toData() : new InvoiceData());
    }

    @GetMapping("/invoices/search")
    @PreAuthorize("hasAuthority('view_invoices')")
    public ResponseEntity<?> getInvoices(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "status", required = false) InvoiceStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<InvoiceData> list = service.searchInvoice(query, status, pageable)
                .map(x -> x.toData());

        auditTrailService.saveAuditTrail("Patient Invoice", "searched invoice");
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

    @GetMapping("/invoices/{invoiceNumber}/invoice-statement")
    public ResponseEntity<InterimInvoice> getInvoiceStatement(@PathVariable(value = "invoiceNumber") String invoiceNumber){
        return ResponseEntity.ok(service.getInvoiceStatement(invoiceNumber));
    }
    @SneakyThrows
    @GetMapping("/invoices/{invoiceNo}/report")
    public void generateReport(HttpServletResponse response,
                               @PathVariable String invoiceNo,
                               @RequestParam(value = "type", required = false, defaultValue = "standard") String type,
                               @RequestParam(value = "format", required = false, defaultValue = "PDF") ExportFormat format) throws FileNotFoundException, JRException {
        JasperPrint print = service.generateInterimStatement(invoiceNo, type);
        //this should be redefined to allow dynamic out
        response.setHeader("Content-Disposition", String.format("attachment; filename=" +"SmartHealth_Interim_Invoice"+ invoiceNo + "." + format.name().toLowerCase()));
        OutputStream out = response.getOutputStream();
        switch (format){
            case PDF:
                JasperExportManager.exportReportToPdfStream(print, out);
                break;
            default:
        }
    }

    @GetMapping("/invoices/{invoiceNo}/reporter")
    public ResponseEntity<byte[]> report(
            @PathVariable String invoiceNo,
            @RequestParam(value = "type", required = false, defaultValue = "standard") String type,
            @RequestParam(value = "format", required = false, defaultValue = "PDF") ExportFormat format
    ){
        byte[] bytes = service.generateInterimStatement(format,invoiceNo, type);
        String contentType = null;
        if(format == ExportFormat.PDF){
            contentType = "application/pdf";
        }else if( format == ExportFormat.XLSX){
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }else if ( format == ExportFormat.HTML){
            contentType = "text/html";
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename("SmartHealth_Invoice_"+invoiceNo + "." + format.name().toLowerCase()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        return ResponseEntity
                .ok()
                .header("Content-Type", contentType + "; charset=UTF-8")
                .headers(headers)
                .body(bytes);
    }
}

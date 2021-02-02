/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.api;

import io.smarthealth.accounting.invoice.service.MiscellaneousInvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.infrastructure.common.ApiResponse;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.smarthealth.accounting.invoice.domain.MiscellaneousInvoice;
import io.smarthealth.accounting.invoice.data.MiscellaneousInvoiceData;
import io.swagger.annotations.Api;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class MiscellaneousInvoiceController {

    private final MiscellaneousInvoiceService service;

    public MiscellaneousInvoiceController(MiscellaneousInvoiceService service) {
        this.service = service;
    }

    @PostMapping("/miscellaneous-invoice")
    public ResponseEntity<MiscellaneousInvoiceData> create(@Valid @RequestBody MiscellaneousInvoiceData data) {
        MiscellaneousInvoice invoice = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(MiscellaneousInvoiceData.map(invoice));
    }

    @GetMapping("/miscellaneous-invoice/{id}")
    public ResponseEntity<MiscellaneousInvoiceData> get(@PathVariable("id") long id) {
        MiscellaneousInvoice invoice = service.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(MiscellaneousInvoiceData.map(invoice));
    }

    @PutMapping("/miscellaneous-invoice/{id}")
    public ResponseEntity<MiscellaneousInvoiceData> update(@PathVariable("id") long id, @Valid @RequestBody MiscellaneousInvoiceData data) {
        MiscellaneousInvoice invoice = service.update(id, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(MiscellaneousInvoiceData.map(invoice));
    }

    @DeleteMapping("/miscellaneous-invoice/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(HttpStatus.ACCEPTED.value(), "Invoice Deleted Successful"));
    }

    @GetMapping("/miscellaneous-invoice")
    public ResponseEntity<?> get(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<MiscellaneousInvoiceData> list = service.get(pageable)
                .map(MiscellaneousInvoiceData::map);

        return ResponseEntity.ok((Pager<List<MiscellaneousInvoiceData>>) PaginationUtil.toPager(list, "Miscellaneous Invoices List"));
    }

}

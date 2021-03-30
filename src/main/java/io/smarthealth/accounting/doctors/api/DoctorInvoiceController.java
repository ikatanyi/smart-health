package io.smarthealth.accounting.doctors.api;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.accounting.doctors.data.DoctorInvoiceStatus;
import io.smarthealth.accounting.doctors.data.ExpenseAdjustmentData;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DoctorInvoiceController {

    private final DoctorInvoiceService doctorService;
    private final BillingService billService;
    private final AuditTrailService auditTrailService;

    @PostMapping("/doctor-invoices")
    @PreAuthorize("hasAuthority('create_doctorInvoices')")
    public ResponseEntity<?> createDoctorInvoice(@Valid @RequestBody DoctorInvoiceData data) {

        DoctorInvoice item = doctorService.createDoctorInvoice(data);
        auditTrailService.saveAuditTrail("Doctor Invoice", "Created Doctor Invoice for doctor" + data.getDoctorName() + " ,for invoice" + data.getInvoiceNumber());
        Pager<DoctorInvoiceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Invoice Successfully Created.");
        pagers.setContent(item.toData());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/doctor-invoices/{id}")
    @PreAuthorize("hasAuthority('view_doctorInvoices')")
    public ResponseEntity<?> getDoctorInvoice(@PathVariable(value = "id") Long id) {
        DoctorInvoice item = doctorService.getDoctorInvoice(id);
        DoctorInvoiceData invoiceData = item.toData();
        PatientBillItem billItem = billService.findBillItemByPatientBill(item.getInvoiceNumber());
        if (billItem != null)
            invoiceData.setReferenceNumber(billItem.getPaymentReference());
        auditTrailService.saveAuditTrail("Doctor Invoice", "Viewed Doctor Invoice with Id " + id);
        return ResponseEntity.ok(invoiceData);
    }

    @PutMapping("/doctor-invoices/{id}")
    @PreAuthorize("hasAuthority('edit_doctorInvoices')")
    public ResponseEntity<?> updateDoctorInvoice(@PathVariable(value = "id") Long id, @Valid @RequestBody DoctorInvoiceData data) {
        DoctorInvoice item = doctorService.updateDoctorInvoice(id, data);
        DoctorInvoiceData invoiceData = item.toData();
        PatientBillItem billItem = billService.findBillItemByPatientBill(item.getInvoiceNumber());
        if (billItem != null)
            invoiceData.setReferenceNumber(billItem.getPaymentReference());
        auditTrailService.saveAuditTrail("Doctor Invoice", "Edited Doctor Invoice with Id " + id);
        return ResponseEntity.ok(invoiceData);
    }

    @DeleteMapping("/doctor-invoices/{id}")
    @PreAuthorize("hasAuthority('delete_doctorInvoices')")
    public ResponseEntity<?> deleteDoctorInvoice(@PathVariable(value = "id") Long id) {
        doctorService.deleteDoctorInvoice(id);
        auditTrailService.saveAuditTrail("Doctor Invoice", "Deleted Doctor Invoice with Id " + id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/doctor-invoices")
    @PreAuthorize("hasAuthority('view_doctorInvoices')")
    public ResponseEntity<?> getDoctorInvoices(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "serviceItem", required = false) String serviceItem,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "invoiceStatus", required = false) DoctorInvoiceStatus invoiceStatus,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<DoctorInvoiceData> list = doctorService.getDoctorInvoices(doctorId, serviceItem, paid, paymentMode, patientNo, invoiceNumber, transactionId, range,invoiceStatus, pageable)
                .map(x -> {
                    DoctorInvoiceData data = x.toData();
                    PatientBillItem item = billService.findBillItemByPatientBill(x.getInvoiceNumber());
                    if (item != null)
                        data.setReferenceNumber(item.getPaymentReference());
                    return data;
                });
        auditTrailService.saveAuditTrail("Doctor Invoice", "Viewed Billed Doctor Invoices");
        Pager<List<DoctorInvoiceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctors Invoices");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/doctor-invoices/{id}/adjustment")
    @PreAuthorize("hasAuthority('edit_doctorInvoices')")
    public ResponseEntity<?> adjustDoctorExpense(@PathVariable(value = "id") Long id,
                                                 @Valid @RequestBody ExpenseAdjustmentData data) {
        DoctorInvoice item = doctorService.adjustDoctorExpense(id, data);
        DoctorInvoiceData invoiceData = item.toData();
        PatientBillItem billItem = billService.findBillItemByPatientBill(item.getInvoiceNumber());
        if (billItem != null)
            invoiceData.setReferenceNumber(billItem.getPaymentReference());
        auditTrailService.saveAuditTrail("Doctor Invoice", "Adjusted Doctor Invoice with Id " + id);
        return ResponseEntity.ok(invoiceData);
    }


}

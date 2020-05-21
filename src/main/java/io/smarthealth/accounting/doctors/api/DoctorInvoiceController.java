package io.smarthealth.accounting.doctors.api;

import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
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
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class DoctorInvoiceController {

    private final DoctorInvoiceService doctorService;

    public DoctorInvoiceController(DoctorInvoiceService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/doctor-invoices")
    @PreAuthorize("hasAuthority('create_doctorInvoices')") 
    public ResponseEntity<?> createDoctorInvoice(@Valid @RequestBody DoctorInvoiceData data) {

        DoctorInvoice item = doctorService.createDoctorInvoice(data);

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
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/doctor-invoices/{id}")
    @PreAuthorize("hasAuthority('edit_doctorInvoices')") 
    public ResponseEntity<?> updateDoctorInvoice(@PathVariable(value = "id") Long id, @Valid @RequestBody DoctorInvoiceData data) {
        DoctorInvoice item = doctorService.updateDoctorInvoice(id, data);
        return ResponseEntity.ok(item.toData());
    }

    @DeleteMapping("/doctor-invoices/{id}")
    @PreAuthorize("hasAuthority('delete_doctorInvoices')") 
    public ResponseEntity<?> deleteDoctorInvoice(@PathVariable(value = "id") Long id) {
        doctorService.deleteDoctorInvoice(id);
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
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<DoctorInvoiceData> list = doctorService.getDoctorInvoices(doctorId, serviceItem, paid, paymentMode, patientNo, invoiceNumber, transactionId, range, pageable)
                .map(x -> x.toData());

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
}

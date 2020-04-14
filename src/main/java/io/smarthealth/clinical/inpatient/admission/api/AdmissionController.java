package io.smarthealth.clinical.inpatient.admission.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.clinical.inpatient.admission.data.AdmissionData;
import io.smarthealth.clinical.inpatient.admission.data.CreateAdmission;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import io.smarthealth.clinical.inpatient.admission.service.AdmissionService;
import io.swagger.annotations.Api;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class AdmissionController {

    private final AdmissionService service;

    public AdmissionController(AdmissionService service) {
        this.service = service;
    }

    @PostMapping("/admissions")
    @ResponseBody
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> createAdmission(@RequestBody @Valid final CreateAdmission data) {
        Admission admission = service.createAdmission(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(admission.toData());
    }

    @GetMapping("/admissions/{id}")
//    @PreAuthorize("hasAuthority('view_admission')")
    public ResponseEntity<?> getAdmission(@PathVariable(value = "id") Long id) {
        Admission admission = service.getAdmissionOrThrow(id);
        return ResponseEntity.ok(admission.toData());
    }
//String patientNo, String admissionNo, Admission.Status status, DateRange range

    @GetMapping("/admissions")
//    @PreAuthorize("hasAuthority('view_admission')")
    public ResponseEntity<?> getAdmissions(
            @RequestParam(value = "patient_no", required = false) String patientNo,
            @RequestParam(value = "admission_no", required = false) String admissionNo,
            @RequestParam(value = "status", required = false) Admission.Status status,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        DateRange range = DateRange.fromIsoString(dateRange);
        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<AdmissionData> list = service.getAdmissions(patientNo, admissionNo, status, range, pageable)
                .map(x -> x.toData());

        Pager<List<AdmissionData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Admission lists");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    //apiadmissions - PUT
    @PutMapping("/admissions/{id}")
//    @PreAuthorize("hasAuthority('update_admission')")
    public ResponseEntity<?> updateAdmission(@PathVariable(value = "id") Long id, AdmissionData data) {
        Admission admission = service.updateAdmission(id, data);
        return ResponseEntity.ok(admission.toData());
    }

    //apiadmissions - Delete
    @DeleteMapping("admissions/{id}")
//    @PreAuthorize("hasAuthority('delete_admission')")
    public ResponseEntity<?> deleteAdmission(@PathVariable(value = "id") Long id) {
        service.deleteAdmission(id);
        return ResponseEntity.ok().build();
    }
}

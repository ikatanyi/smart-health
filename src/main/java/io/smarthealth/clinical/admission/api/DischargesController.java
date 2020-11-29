package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.DischargeData;
import io.smarthealth.clinical.admission.data.DischargeDiagnosis;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.service.DischargeService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class DischargesController {

    private final DischargeService service;

    @PostMapping("/discharge-summary")
//    @PreAuthorize("hasAuthority('create_discharge-summary')")
    public ResponseEntity<Pager<DischargeData>> createDischargeSummary(@Valid @RequestBody DischargeData summaryData) {

        DischargeSummary result = service.createDischarge(summaryData);

        Pager<DischargeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Discharge Created successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/discharge-summary/{id}")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public ResponseEntity<DischargeData> getDischarge(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(service.getDischargeById(id).toData());
    }

    @GetMapping("/discharge-summary/{visitNo}/visit")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public ResponseEntity<DischargeData> getDischargeByVisit(@PathVariable(value = "visitNo") String visitNo) {
        DischargeSummary discharge = service.getDischargeByVisit(visitNo);
        if(discharge!=null){
            return ResponseEntity.ok(discharge.toData());
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/discharge-summary/{admissionNo}/diagnosis")
//    @PreAuthorize("hasAuthority('edit_discharge-summary')")
    public ResponseEntity<DischargeDiagnosis> updateDischargeDiagnosis(@PathVariable(value = "admissionNo") String admissionNo, @Valid @RequestBody DischargeDiagnosis diagnosisData) {
        PatientDiagnosis diag = service.updateDiagnosis(admissionNo, diagnosisData);
        return ResponseEntity.ok(diag.toData());
    }

    @GetMapping("/discharge-summary")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public ResponseEntity<?> getDischarges(
            @RequestParam(value = "dischargeNo", required = false) final String dischargeNo,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<DischargeData> list = service.getDischarges(dischargeNo, patientNo, term, range, pageable).map(u -> u.toData());

        Pager<List<DischargeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("DischargeSummaries");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/discharge-summary/{id}")
//    @PreAuthorize("hasAuthority('create_discharge-summary')")
    public ResponseEntity<Pager<DischargeData>> updateDischarge(@PathVariable("id") Long id, @Valid @RequestBody DischargeData summaryData) {

        DischargeSummary result = service.updateDischarge(id, summaryData);

        Pager<DischargeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("DischargeSummary Updated successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

}

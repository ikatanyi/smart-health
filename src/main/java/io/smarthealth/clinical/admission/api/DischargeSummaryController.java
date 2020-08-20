package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.DischargeSummaryData;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.service.DischargeSummaryService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class DischargeSummaryController {

    private final DischargeSummaryService service;

    @PostMapping("/discharge-summary")
//    @PreAuthorize("hasAuthority('create_discharge-summary')")
    public ResponseEntity<?> createDischargeSummary(@Valid @RequestBody DischargeSummaryData summaryData) {

        DischargeSummaryData result = service.createDischargeSummary(summaryData).toData();

        Pager<DischargeSummaryData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Discharge Completed successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/discharge-summary/{id}")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public DischargeSummary getItem(@PathVariable(value = "id") Long id) {
        return service.findDischargeSummaryById(id);
    }

    @GetMapping("/discharge-summary")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public ResponseEntity<?> getAllDischargeSummarys(
            @RequestParam(value = "dischargeNo", required = false) final String dischargeNo,
            @RequestParam(value = "doctorId", required = false) final Long doctorId,
            @RequestParam(value = "patientId", required = false) final Long patientId,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<DischargeSummaryData> list = service.fetchDischargeSummarys(dischargeNo, doctorId, patientId, term, range, pageable).map(u -> u.toData());

        Pager<List<DischargeSummaryData>> pagers = new Pager();
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
    public ResponseEntity<?> updateDischargeSummary(@PathVariable("id") Long id, @Valid @RequestBody DischargeSummaryData summaryData) {

        DischargeSummaryData result = service.updateDischargeSummary(id, summaryData).toData();

        Pager<DischargeSummaryData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("DischargeSummary Updated successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

}

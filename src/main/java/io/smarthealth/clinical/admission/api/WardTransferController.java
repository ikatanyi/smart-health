package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.WardTransferData;
import io.smarthealth.clinical.admission.domain.WardTransfer;
import io.smarthealth.clinical.admission.service.WardTransferService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
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
public class WardTransferController {

    private final WardTransferService service;
    private final AuditTrailService auditTrailService;
    
    @PostMapping("/ward-transfer")
//    @PreAuthorize("hasAuthority('create_ward-transfer')")
    public ResponseEntity<?> createWardTransfer(@Valid @RequestBody WardTransferData transferData) {

        WardTransferData result = service.createWardTransfer(transferData).todata();
        auditTrailService.saveAuditTrail("Admission", "Transferred patient to ward "+result.getWardName()+" room "+result.getRoomName()+" bed "+result.getBedName());
        Pager<WardTransferData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("WardTransfer completed successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/ward-transfer/{id}")
//    @PreAuthorize("hasAuthority('view_ward-transfer')")
    public WardTransfer getItem(@PathVariable(value = "id") Long id) {
        auditTrailService.saveAuditTrail("Admission", "Searched  patient Transferred identified by id "+id);
        return service.findWardTransferById(id);
    }

    @GetMapping("/ward-transfer")
//    @PreAuthorize("hasAuthority('view_ward-transfer')")
    public ResponseEntity<?> getAllWardTransfers(
            @RequestParam(value = "wardId", required = false) final String dischargeNo,
            @RequestParam(value = "roomId", required = false) final Long doctorId,
            @RequestParam(value = "bedId", required = false) final Long bedId,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "admissionNo", required = false) final String admissionNo,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<WardTransferData> list = service.fetchWardTransfers(bedId, bedId, bedId, patientNo, admissionNo, range, term, pageable).map(u ->{
            auditTrailService.saveAuditTrail("Admission", "viewed patient trnasfer to ward "+u.getWard().getName()+" room "+u.getRoom().getName()+" bed "+u.getBed().getName());
            return u.todata();
                });

        Pager<List<WardTransferData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Ward Transfers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/ward-transfer/{id}")
//    @PreAuthorize("hasAuthority('create_ward-transfer')")
    public ResponseEntity<?> updateWardTransfer(@PathVariable("id") Long id, @Valid @RequestBody WardTransferData transferData) {

        WardTransferData result = service.updateWardTransfer(id, transferData).todata();
        auditTrailService.saveAuditTrail("Admission", "Edited  patient ward Transferred identified by id "+id);
        Pager<WardTransferData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("WardTransfer Updated successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

}

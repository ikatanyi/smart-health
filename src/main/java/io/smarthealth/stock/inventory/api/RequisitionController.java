package io.smarthealth.stock.inventory.api;

import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.stock.inventory.data.RequisitionData;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.inventory.service.RequisitionService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Api
@RestController
@RequestMapping("/api")
public class RequisitionController {

    private final RequisitionService service;
    private final AuditTrailService auditTrailService;

    public RequisitionController(RequisitionService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/requisitions")
    @PreAuthorize("hasAuthority('create_requisition')")
    public ResponseEntity<?> createRequisition(@Valid @RequestBody RequisitionData orderData) {

        RequisitionData result = service.createRequisition(orderData);

        Pager<RequisitionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Requisition created successful");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Inventory", "Created an inventory requistion "+result.getRequestionNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/requisitions/{id}")
    @PreAuthorize("hasAuthority('view_requisition')")
    public RequisitionData getRequisition(@PathVariable(value = "id") Long code) {
        Requisition po = service.findOneWithNoFoundDetection(code);
        auditTrailService.saveAuditTrail("Inventory", "Viewed an inventory requistion identified by id "+code);
        return RequisitionData.map(po);
    }

    @GetMapping("/requisitions")
    @PreAuthorize("hasAuthority('view_requisition')")
    public ResponseEntity<?> getAllRequisitions(
            @RequestParam(value = "status", required = false) List<RequisitionStatus> status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<RequisitionData> list = service.getRequisitions(status, pageable)
                .map(u -> RequisitionData.map(u));

        Pager<List<RequisitionData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Requisitions");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Inventory", "Viewed all requisitions done");
        return ResponseEntity.ok(pagers);
    }
}

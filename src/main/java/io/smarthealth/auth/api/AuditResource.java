package io.smarthealth.auth.api;

import io.smarthealth.auth.service.AuditEventService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.infrastructure.utility.web.ResponseUtil;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * REST controller for getting the audit events.
 */
@RestController
@RequestMapping("/management/audits")
public class AuditResource {

    private final AuditEventService auditEventService;

    public AuditResource(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    /**
     * GET /audits : get a page of AuditEvents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of
     * AuditEvents in body
     */
    @GetMapping
    public ResponseEntity<Pager<List<AuditEvent>>> getAll(Pageable pageable) {
        Page<AuditEvent> list = auditEventService.findAll(pageable);
        Pager<List<AuditEvent>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Suppliers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    /**
     * GET /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of AuditEvents to get
     * @param toDate the end of the time period of AuditEvents to get
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of
     * AuditEvents in body
     */
    @GetMapping(params = {"fromDate", "toDate"})
    public ResponseEntity<Pager<List<AuditEvent>>> getByDates(
            @RequestParam(value = "fromDate") LocalDate fromDate,
            @RequestParam(value = "toDate") LocalDate toDate,
            Pageable pageable) {

        Page<AuditEvent> list = auditEventService.findByDates(
                fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant(),
                pageable);

        Pager<List<AuditEvent>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Suppliers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);

    }

    /**
     * GET /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the ResponseEntity with status 200 (OK) and the AuditEvent in
     * body, or status 404 (Not Found)
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<AuditEvent> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(auditEventService.find(id));
    }
}

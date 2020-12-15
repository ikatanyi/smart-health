package io.smarthealth.security.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.security.data.AuditTrailData;
import io.smarthealth.security.domain.AuditTrail;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuditTrailController {

    private final AuditTrailService service;
    

    @PostMapping("/audit-trail")
    public ResponseEntity<?> createAuditTrail(@Valid @RequestBody AuditTrailData data) {
        AuditTrail auditTrail = service.createAuditTrail(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(auditTrail.toData());
    }

    @GetMapping("/audit-trail/{id}")
    @PreAuthorize("hasAuthority('view_audit_trail')")
    public AuditTrailData getAuditTrail(@PathVariable(value = "id") Long id) {
        AuditTrail auditTrail = service.find(id);
        return auditTrail.toData();
    }


    @GetMapping("/audit-trail")
    @PreAuthorize("hasAuthority('view_audit_trail')")
    public ResponseEntity<?> getAllCodes(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        List<AuditTrailData> lists = service.findAll(range,name, pageable)
                .stream().map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }

}

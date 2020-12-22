package io.smarthealth.debtor.claim.writeOff.api;

import io.smarthealth.debtor.claim.writeOff.data.WriteOffData;
import io.smarthealth.debtor.claim.writeOff.service.WriteOffService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/")
public class WriteOffController {

    private final WriteOffService writeOffService;
    private final AuditTrailService auditTrailService;

    public WriteOffController(WriteOffService writeOffService, AuditTrailService auditTrailService) {
        this.writeOffService = writeOffService;
        this.auditTrailService = auditTrailService;
    }

    

    
    @PostMapping("/writeOff")
    @PreAuthorize("hasAuthority('create_writeoff')")
    public ResponseEntity<?> createWriteOff(@Valid @RequestBody WriteOffData writeOffData) {

        WriteOffData remittance = writeOffService.map(writeOffService.createWriteOff(writeOffData));

        Pager<WriteOffData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("WriteOff successfully Created.");
        pagers.setContent(remittance);
        auditTrailService.saveAuditTrail("Write Off", "Created a write-off for invoice "+remittance.getInvoiceNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/writeOff/{id}")
    @PreAuthorize("hasAuthority('view_writeoff')")
    public WriteOffData getWriteOff(@PathVariable(value = "id") Long id) {
        WriteOffData writeOff = writeOffService.map(writeOffService.getWriteOffByIdWithFailDetection(id));
        return writeOff;
    }

    @PutMapping("/writeOff/{id}")
    @PreAuthorize("hasAuthority('edit_writeoff')")
    public WriteOffData updateRemitance(@PathVariable(value = "id") Long id, WriteOffData writeOffData) {
        WriteOffData writeOff = writeOffService.map(writeOffService.updateWriteOff(id, writeOffData));
        auditTrailService.saveAuditTrail("Write Off", "Viewed a write-off for invoice "+writeOff.getInvoiceNo());
        return writeOff;
    }

    @GetMapping("/writeOff")
    @PreAuthorize("hasAuthority('view_writeoff')")
    public ResponseEntity<?> getAllWriteOffes(
            @RequestParam(value = "payerId", required = false) Long payerId, 
            @RequestParam(value = "schemeId", required = false) Long schemeId, 
            @RequestParam(value = "invoiceNo", required = false) Long invoiceNo, 
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<WriteOffData> list = writeOffService.getAllWriteOff(payerId, schemeId, dateRange, range, pageable)
                .map(disp -> writeOffService.map(disp));
        auditTrailService.saveAuditTrail("Write Off", "Viewed all write-off ");
        Pager<List<WriteOffData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("WriteOffes");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}

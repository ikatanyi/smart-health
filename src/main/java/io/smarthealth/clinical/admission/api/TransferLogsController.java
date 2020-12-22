package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.TransferLogsData;
import io.smarthealth.clinical.admission.service.TransferLogsService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class TransferLogsController {

    private final TransferLogsService service;
    private final AuditTrailService auditTrailService;    
     

    @GetMapping("/transfer-logs")
//    @PreAuthorize("hasAuthority('view_transfer-logs')")
    public ResponseEntity<?> getAllTransfers(
           
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<TransferLogsData> list = service.fetchTransferLogs(pageable).map(u -> {
            auditTrailService.saveAuditTrail("Admission", "viewed patient transfer from bed"+u.getFromBed()+" to bed "+u.getToBed());
            return u.toData();
                });
        
        
        Pager<List<TransferLogsData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Transfer Logs");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }  
    
}

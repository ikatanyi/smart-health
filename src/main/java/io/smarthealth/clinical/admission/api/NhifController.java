package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.NhifRebateData;
import io.smarthealth.clinical.admission.domain.NhifRebate;
import io.smarthealth.clinical.admission.service.NhifRebateService;
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
public class NhifController {

    private final NhifRebateService service;
     
    @GetMapping("/nhif-rebate/{id}")
//    @PreAuthorize("hasAuthority('view_nhif-rebate')")
    public NhifRebate getNhifRebate(@PathVariable(value = "id") Long code) {
        NhifRebate rebate = service.getNhifRebate(code);
        return  rebate;
    }

    @GetMapping("/nhif-rebate")
//    @PreAuthorize("hasAuthority('view_nhif-rebate')")
    public ResponseEntity<?> getAllNhifRebates(
            @RequestParam(value = "admissionNumber", required = false) final String admissionNumber,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "memberNumber", required = false) final String memberNumber,
            @RequestParam(value = "dateRange", required = false) final String dateRange,        
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<NhifRebateData> list = service.fetchNhifRebates(admissionNumber, patientNumber, memberNumber, range, pageable).map(u -> u.toData());
        
        
        Pager<List<NhifRebateData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("NhifRebates");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/nhif-rebate")
//     @PreAuthorize("hasAuthority('create_nhif-rebate')")
    public ResponseEntity<?> createNhifRebate(@Valid @RequestBody NhifRebateData nhifRebateData) {
        
        NhifRebateData result = service.createNhifRebate(nhifRebateData).toData();
        
        Pager<NhifRebateData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Nhif-Rebate created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }  
    
}

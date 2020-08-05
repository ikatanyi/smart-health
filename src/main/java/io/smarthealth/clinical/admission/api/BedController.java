package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.BedData;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import io.smarthealth.clinical.admission.service.BedService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
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
public class BedController {

    private final BedService service;
     
    @GetMapping("/bed/{id}")
//    @PreAuthorize("hasAuthority('view_bed')")
    public Bed getItem(@PathVariable(value = "id") Long code) {
        Bed bed = service.getBed(code);
        return  bed;
    }

    @GetMapping("/bed")
//    @PreAuthorize("hasAuthority('view_bed')")
    public ResponseEntity<?> getAllBeds(
            @RequestParam(value = "active", required = false, defaultValue = "false") final boolean active,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "status", required = false) final Status status,
            @RequestParam(value = "room_id", required = false) final Long roomId,          
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<BedData> list = service.fetchBeds(name, status, active, roomId, term, pageable).map(u -> u.toData());
        
        
        Pager<List<BedData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Beds");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/bed")
//     @PreAuthorize("hasAuthority('create_bed')")
    public ResponseEntity<?> createBed(@Valid @RequestBody BedData bedData) {
        
        BedData result = service.createBed(bedData).toData();
        
        Pager<BedData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bed created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/bed/{id}")
    @PreAuthorize("hasAuthority('create_bed')")
    public ResponseEntity<?> updateBed(@PathVariable("id") Long id, @Valid @RequestBody BedData bedData) {
        
        BedData result = service.updateBed(id,bedData).toData();
        
        Pager<BedData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bed Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    
    
}

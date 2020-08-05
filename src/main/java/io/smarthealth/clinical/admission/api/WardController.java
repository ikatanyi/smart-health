package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.WardData;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.admission.service.WardService;
import io.smarthealth.infrastructure.common.PaginationUtil;
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
public class WardController {

    private final WardService service;
     
    @GetMapping("/ward/{id}")
//    @PreAuthorize("hasAuthority('view_ward')")
    public Ward getItem(@PathVariable(value = "id") Long code) {
        Ward ward = service.getWard(code);
        return  ward;
    }

    @GetMapping("/ward")
//    @PreAuthorize("hasAuthority('view_ward')")
    public ResponseEntity<?> getAllWards(
            @RequestParam(value = "active", required = false, defaultValue = "false") final boolean active,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<WardData> list = service.fetchWards(name, active,term, pageable).map(u -> u.toData());
        
        
        Pager<List<WardData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Wards");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/ward")
//     @PreAuthorize("hasAuthority('create_ward')")
    public ResponseEntity<?> createWard(@Valid @RequestBody WardData wardData) {
        
        WardData result = service.createWard(wardData).toData();
        
        Pager<WardData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Ward created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/ward/{id}")
    @PreAuthorize("hasAuthority('create_ward')")
    public ResponseEntity<?> updateWard(@PathVariable("id") Long id, @Valid @RequestBody WardData wardData) {
        
        WardData result = service.updateWard(id,wardData).toData();
        
        Pager<WardData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Ward Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    
    
}

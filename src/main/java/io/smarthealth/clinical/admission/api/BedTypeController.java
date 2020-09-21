package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.service.BedTypeService;
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
public class BedTypeController {

    private final BedTypeService service;
     
    @GetMapping("/bed-type/{id}")
//    @PreAuthorize("hasAuthority('view_bed-type')")
    public BedType getItem(@PathVariable(value = "id") Long code) {
        return service.getBedType(code);
    }

    @GetMapping("/bed-type")
//    @PreAuthorize("hasAuthority('view_bed-type')")
    public ResponseEntity<?> getAllBedTypes(
            @RequestParam(value = "active", required = false, defaultValue = "false") final boolean active,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<BedTypeData> list = service.fetchBedTypes(name, active,term, pageable).map(u -> u.toData());
        
        
        Pager<List<BedTypeData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("BedTypes");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/bed-type")
//     @PreAuthorize("hasAuthority('create_bed-type')")
    public ResponseEntity<?> createBedType(@Valid @RequestBody BedTypeData bedTypeData) {
        
        BedTypeData result = service.createBedType(bedTypeData).toData();
        
        Pager<BedTypeData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("BedType created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/bed-type/{id}")
//    @PreAuthorize("hasAuthority('create_bedtype')")
    public ResponseEntity<?> updateBedType(@PathVariable("id") Long id, @Valid @RequestBody BedTypeData bedTypeData) {
        
        BedTypeData result = service.updateBedType(id,bedTypeData).toData();
        
        Pager<BedTypeData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("BedType Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/bed-type/{bedTypeId}/charges")
//    @PreAuthorize("hasAuthority('create_bedtype')")
    public ResponseEntity<?> addCharge(@PathVariable("bedTypeId") Long id, @Valid @RequestBody BedChargeData bedChargeData) {
        
        BedTypeData result = service.addBedCharge(id,bedChargeData).toData();
        
        Pager<BedTypeData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("BedType Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    
    
}

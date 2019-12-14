package io.smarthealth.stock.item.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.data.UomData;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.service.UomService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class UomRestController {

    private final UomService service;

    public UomRestController(UomService itemService) {
        this.service = itemService;
    } 
     
    @GetMapping("/uom/{id}")
    public Uom getItem(@PathVariable(value = "id") Long code) {
        Uom uom = service.fetchUomById(code);
        return  uom;
    }

    @GetMapping("/uom")
    public ResponseEntity<?> getAllItems(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<UomData> list = service.fetchAllUom(pageable).map(u -> UomData.map(u));
        
        
        Pager<List<UomData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Unit of Measure");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
     @PostMapping("/uom")
    public ResponseEntity<?> createUom(@Valid @RequestBody UomData uomData) {
        
        UomData result = UomData.map(service.createUom(uomData));
        
        Pager<UomData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Uom created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
}

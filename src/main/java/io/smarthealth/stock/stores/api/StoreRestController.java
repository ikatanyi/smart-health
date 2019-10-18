package io.smarthealth.stock.stores.api;
 
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class StoreRestController {
    private final StoreService service;

    public StoreRestController(StoreService service) {
        this.service = service;
    }
    @PostMapping("/inventory/stores")
    public ResponseEntity<?> createStore(@Valid @RequestBody Store store) {
        
        Store result = service.createStore(store);
        
        Pager<Store> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Store created success");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    @GetMapping("/inventory/stores/{id}")
    public ResponseEntity<?> getStore(@PathVariable(value = "id") Long code) {
        Store tax = service.getStore(code)
                .orElseThrow(() -> APIException.notFound("Store with id  {0} not found.", code));
        return ResponseEntity.ok(tax);
    }
    @GetMapping("/inventory/stores")
    public ResponseEntity<?> getAllStorees( 
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<Store> list = service.fetchAllStores(pageable); 
        Pager<List<Store>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Storees");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
}

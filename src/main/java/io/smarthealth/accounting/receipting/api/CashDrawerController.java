package io.smarthealth.accounting.receipting.api;

import io.smarthealth.accounting.receipting.domain.CashDrawer;
import io.smarthealth.accounting.receipting.service.CashDrawerService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
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
public class CashDrawerController {

    private final CashDrawerService service;

    public CashDrawerController(CashDrawerService cashDrawerServices) {
        this.service = cashDrawerServices;
    }

    @PostMapping("/cashdrawers")
    public ResponseEntity<?> createCashDrawer(@Valid @RequestBody CashDrawer cashDrawer) {

        CashDrawer result = service.createCashDrawer(cashDrawer);

        Pager<CashDrawer> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("CashDrawer Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/cashdrawers/{id}")
    public ResponseEntity<?> getCashDrawer(@PathVariable(value = "id") Long code) {
        CashDrawer result = service.getCashDrawer(code);
        Pager<CashDrawer> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("CashDrawer Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @PutMapping("/cashdrawers/{id}")
    public ResponseEntity<?> updatecashDrawer(@PathVariable(value = "id") Long id, CashDrawer data) {
        CashDrawer cashDrawer = service.updateCashDrawer(id, data);
        return ResponseEntity.ok(cashDrawer);
    }

    @GetMapping("/cashdrawers")
    public ResponseEntity<?> getAllCashDraweres(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "1000") Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<CashDrawer> list = service.fetchAllCashDrawers(pageable);
        Pager<List<CashDrawer>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Cash Drawers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}

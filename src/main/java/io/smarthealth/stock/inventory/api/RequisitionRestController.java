package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.RequisitionData;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.service.RequisitionService;
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
@Slf4j
@Api
@RestController
@RequestMapping("/api")
public class RequisitionRestController {

    private final RequisitionService service;

    public RequisitionRestController(RequisitionService service) {
        this.service = service;
    }

    @PostMapping("/requisitions")
    public ResponseEntity<?> createRequisition(@Valid @RequestBody RequisitionData orderData) {

        RequisitionData result = service.createRequisition(orderData);

        Pager<RequisitionData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Requisition created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/requisitions/{id}")
    public RequisitionData getRequisition(@PathVariable(value = "id") Long code) {
        Requisition po = service.findOneWithNoFoundDetection(code);
        return RequisitionData.map(po);
    }

    @GetMapping("/requisitions")
    public ResponseEntity<?> getAllRequisitions(  
            @RequestParam(value = "status", required = false) final String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<RequisitionData> list = service.getRequisitions(status, pageable)
                .map(u -> RequisitionData.map(u));

        Pager<List<RequisitionData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Requisitions");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}

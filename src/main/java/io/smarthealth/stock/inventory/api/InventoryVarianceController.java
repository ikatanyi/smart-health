package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.InventoryVarianceData;
import io.smarthealth.stock.inventory.data.RequisitionData;
import io.smarthealth.stock.inventory.domain.InventoryVariance;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.inventory.service.RequisitionService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
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
@Slf4j
@Api
@RestController
@RequestMapping("/api/v1")
public class InventoryVarianceController {

    private final InventoryService service;

    public InventoryVarianceController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/inventory-variance")
    public ResponseEntity<?> createInventoryVariance(@Valid @RequestBody InventoryVarianceData varianceData) {

        InventoryVarianceData result = service.map(service.saveStockVariance(varianceData));

        Pager<InventoryVarianceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Inventory created successful");
        pagers.setContent(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/inventory-variance/{id}")
    public InventoryVarianceData getVariance(@PathVariable(value = "id") Long code) {
        InventoryVariance po = service.findOneWithNotFoundDetection(code);
        return service.map(po);
    }

    @GetMapping("/inventory-varinace")
    public ResponseEntity<?> getAllVariances(  
            @RequestParam(value = "from", required = false) final LocalDate from,
            @RequestParam(value = "to", required = false) final LocalDate to,
            @RequestParam(value = "storeId", required = false) final Long storeId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<InventoryVarianceData> list = service.getAllStockVariances(from, to, storeId, pageable);
        Pager<List<InventoryVarianceData>> pagers = new Pager();
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

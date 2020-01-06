package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.StockAdjustmentData;
import io.smarthealth.stock.inventory.domain.StockAdjustment;
import io.smarthealth.stock.inventory.service.InventoryAdjustmentService;
import io.smarthealth.stock.inventory.service.InventoryService;
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
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequiredArgsConstructor
@RequestMapping("/api")
public class InventoryAdjustmentController {

    private final InventoryAdjustmentService service;

    @PostMapping("/inventory-adjustment")
    public ResponseEntity<?> createStockAdjustment(@Valid @RequestBody StockAdjustmentData data) {

        StockAdjustment result = service.createStockAdjustment(data);

        Pager<StockAdjustmentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Adjustment successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/inventory-adjustment/{id}")
    public StockAdjustmentData searchStockAdjustment(@PathVariable(value = "id") Long id) {
        StockAdjustmentData stocks = service.getStockAdjustment(id).toData();
        return stocks;
    }
 
    @GetMapping("/inventory-adjustment")
    public ResponseEntity<?> getStockAdjustment(
            @RequestParam(value = "store", required = false) final Long store,
            @RequestParam(value = "item", required = false) final Long item,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
        Page<StockAdjustmentData> list = service.getStockAdjustments(store, item, range, pageable);

        Pager<List<StockAdjustmentData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Stock Adjustment ");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

}

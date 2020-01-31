package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.CreateStockEntry;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
import io.smarthealth.stock.inventory.data.TransData;
import io.smarthealth.stock.inventory.domain.StockEntry;
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
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/inventory-entries")
    public ResponseEntity<?> createStockEntry(@Valid @RequestBody CreateStockEntry stocks) {

        String result = service.createStockEntry(stocks);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Movement successful");
        pagers.setContent(new TransData(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
     @PostMapping("/inventory-entries/supplier")
    public ResponseEntity<?> createStockEntrySupplier(@Valid @RequestBody SupplierStockEntry stocks) {

        String result = service.receiveSupplierStocks(stocks);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Movement successful");
        pagers.setContent(new TransData(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    //inventories/{id}
    @GetMapping("/inventory-entries/{id}")
    public StockEntryData searchStockEntry(@PathVariable(value = "id") Long id) {
        StockEntryData stocks = service.getStockEntry(id).toData();
        return stocks;
    }

    @GetMapping("/inventory-entries")
    public ResponseEntity<?> getAllStockEntries(
            @RequestParam(value = "store", required = false) final String store,
            @RequestParam(value = "item", required = false) final String item,
            @RequestParam(value = "reference", required = false) final String referenceNumber,
            @RequestParam(value = "transactionId", required = false) final String transactionId,
            @RequestParam(value = "deliveryNo", required = false) final String deliveryNumber,
            @RequestParam(value = "purpose", required = false) final String purpose,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<StockEntryData> list = service
                .getStockEntries(store, item, referenceNumber, transactionId, deliveryNumber, purpose, type, range, pageable)
                .map(u -> StockEntryData.map(u));

        Pager<List<StockEntryData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Stock Movements");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
 
    
}

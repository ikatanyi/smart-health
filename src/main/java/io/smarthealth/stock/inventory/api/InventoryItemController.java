package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.CreateInventoryItem;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.service.InventoryItemService;
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
public class InventoryItemController {

    private final InventoryItemService service;

    @PostMapping("/inventoryItem")
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody CreateInventoryItem itemData) {

//        InventoryItem result = 
                service.createInventoryItem(itemData);

//        Pager<InventoryItemData> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Inventory Item created successful");
//        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/inventoryItem/{id}/store/{storeId}")
    public InventoryItemData getInventoryItem(@PathVariable(value = "id") Long code, @PathVariable(value = "storeId") Long storeId) {
        InventoryItem inventoryItem = service.getInventoryItemOrThrow(code, storeId);
        return inventoryItem.toData();
    }

    @GetMapping("/inventoryItems")
    public ResponseEntity<?> getInventoryItems(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "item", required = false) final String item,
            @RequestParam(value = "store", required = false) final Long storeId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<InventoryItemData> list = service.getInventoryItems(storeId, item, pageable, includeClosed);

//        Page<InventoryItemData> list = service.getPricebooks(category, type, pageable, includeClosed).map(u -> InventoryItemData.map(u));
        Pager<List<InventoryItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Pricebook");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

//    @GetMapping("/inventory-balances")
//    public ResponseEntity<?> getAllItemsBalance(
//            @RequestParam(value = "store", required = true) final Long store,
//            @RequestParam(value = "item", required = false) final Long item,
//            @RequestParam(value = "dateRange", required = false) String dateRange,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer size) {
//
//        Pageable pageable = PaginationUtil.createPage(page, size);
//        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
//        Page<InventoryBalanceData> list = service.getInventoryBalance(store, item, range, pageable);
//
//        Pager<List<InventoryBalanceData>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(list.getContent());
//        PageDetails details = new PageDetails();
//        details.setPage(list.getNumber() + 1);
//        details.setPerPage(list.getSize());
//        details.setTotalElements(list.getTotalElements());
//        details.setTotalPage(list.getTotalPages());
//        details.setReportName("Stock Inventory Balance");
//        pagers.setPageDetails(details);
//
//        return ResponseEntity.ok(pagers);
//    }
    //stocks adjustment
}

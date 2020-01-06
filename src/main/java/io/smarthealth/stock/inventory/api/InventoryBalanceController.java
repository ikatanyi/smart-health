package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.InventoryBalanceData;
import io.smarthealth.stock.inventory.service.InventoryBalanceService;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class InventoryBalanceController {

    private final InventoryBalanceService service;
 
//     Stock Balances 
    //inventories/{itemId}/balance
//    @GetMapping("/inventory-balances/{itemId}")
//    public ResponseEntity<?> searchItemBalance(
//            @PathVariable(value = "itemId") final Long itemId,
//            @RequestParam(value = "store", required = false) final Long store,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer size) {
//
//        Pageable pageable = PaginationUtil.createPage(page, size);
//        Page<InventoryBalanceData> list = service.getInventoryBalance(itemId, store, pageable);
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
//        details.setReportName("Stock Items Balance");
//        pagers.setPageDetails(details);
//
//        return ResponseEntity.ok(pagers);
//    }

    //inventories/all/balance
    
    @GetMapping("/inventory-balances")
    public ResponseEntity<?> getAllItemsBalance(
            @RequestParam(value = "store", required = false) final Long store,
            @RequestParam(value = "item", required = false) final Long item,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<InventoryBalanceData> list = service.getInventoryBalance(store, item, range, pageable);

        Pager<List<InventoryBalanceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Stock Items Balance");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    //stocks adjustment
    
}

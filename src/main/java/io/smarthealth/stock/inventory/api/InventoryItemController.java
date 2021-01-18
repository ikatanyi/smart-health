package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.CreateInventoryItem;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.data.ItemDTO;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.service.InventoryItemService;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.stock.item.domain.Item;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuditTrailService auditTrailService;

    @PostMapping("/inventoryItem")
    @PreAuthorize("hasAuthority('create_inventoryitem')")
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
    @PreAuthorize("hasAuthority('view_inventoryItem')")
    public InventoryItemData getInventoryItem(@PathVariable(value = "id") Long code, @PathVariable(value = "storeId") Long storeId) {
        InventoryItem inventoryItem = service.getInventoryItem(code, storeId).orElse(null);
        Item item = inventoryItem != null ? inventoryItem.getItem() : null;
        if (item != null) {
            auditTrailService.saveAuditTrail("Inventory", "viewed inventory item " + inventoryItem.getItem().getItemName() + "at the store identified by id " + storeId);
        }
        return inventoryItem != null ? inventoryItem.toData() : null;
    }

    @GetMapping("/inventoryItem/store/{storeId}")
    @PreAuthorize("hasAuthority('view_inventoryItem')")
    public List<InventoryItemData> getInventoryItemLists(@PathVariable(value = "id") Long code, @RequestParam(value = "items", required = true) final List<ItemDTO> item) {
        List<InventoryItemData> inventoryItem = service.getInventoryItemList(code, item)
                .stream()
                .map(x -> x.toData()).collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Inventory", "viewed inventory items at store identified by id " + code);
        return inventoryItem;
    }

    @GetMapping("/inventoryItems")
    @PreAuthorize("hasAuthority('view_inventoryItem')")
    public ResponseEntity<?> getInventoryItems(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "search", required = false) final String search,
            @RequestParam(value = "item_id", required = false) final Long itemId,
            @RequestParam(value = "store_id", required = false) final Long storeId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<InventoryItemData> list = service.getInventoryItems(storeId, itemId, search, includeClosed, pageable).map(itm -> itm.toData());

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
        details.setReportName("Inventory Items");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Inventory", "viewed all inventory items ");
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/inventoryItem/{itemCode}/item-count")
    @PreAuthorize("hasAuthority('view_inventoryItem')")
    public Integer getInventoryItemCount(@PathVariable(value = "itemCode") String itemCode) {
        auditTrailService.saveAuditTrail("Inventory", "viewed inventory item count for item identified by " + itemCode);
        return service.getItemCount(itemCode);
    }

    @GetMapping("/inventoryItem/{itemCode}/store/{storeId}/item-count")
    @PreAuthorize("hasAuthority('view_inventoryItem')")
    public Integer getInventoryItemCountByStore(@PathVariable(value = "itemCode") String itemCode, @PathVariable(value = "storeId") Long storeId) {
        auditTrailService.saveAuditTrail("Inventory", "viewed inventory item count for item identified by " + itemCode + " for store identified by " + storeId);
        return service.getItemCountByItemAndStore(itemCode, storeId);
    }
    //create endpoint to update stock balances if they are out of sync

    @GetMapping("/inventoryItem/updateBalance")
    public ResponseEntity<Void> updateBalances(
            @RequestParam(value = "itemId", required = false) final Long itemId,
            @RequestParam(value = "storeId", required = false) final Long storeId
    ) {
        service.doUpdateBalance(itemId, storeId);
        return ResponseEntity.accepted().build();
    }
}

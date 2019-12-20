package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Kennedy.Ikatanyi
 */
@Slf4j
@Api
@RestController
@RequestMapping("/api/v1")
public class InventoryRestController {

    private final InventoryService service;
    

    public InventoryRestController(InventoryService service) {
        this.service = service;
    }


    @GetMapping("/inventoryItem/{id}")
    public InventoryItemData getInventory(@PathVariable(value = "id") Long id) {
        InventoryItem inventoryItem = service.findInventoryItemWithNotFoundDetection(id);
        return service.map(inventoryItem);
    }

    @GetMapping("/inventory")
    public ResponseEntity<?> getAllRequisitions(  
            @RequestParam(value = "status", required = false) final String status,
            @RequestParam(value = "from", required = false) final LocalDate from, 
            @RequestParam(value = "to", required = false) final LocalDate to,
            @RequestParam(value = "storeId", required = false) final Long storeId,
            @RequestParam(value = "moveType", required = false) final String moveType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<InventoryItemData> list = service.getAllInventoryItems(from, to, moveType, storeId, pageable);
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

        return ResponseEntity.ok(pagers);
    }
}

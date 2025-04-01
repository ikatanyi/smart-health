package io.smarthealth.stock.item.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.data.ReorderLevelData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemMetadata;
import io.smarthealth.stock.item.domain.ReorderRule;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author Kennedy
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class ItemRestController {

    private final ItemService service;
    private final AuditTrailService auditTrailService;

    public ItemRestController(ItemService itemService, AuditTrailService auditTrailService) {
        this.service = itemService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('create_items')")
    public ResponseEntity<?> createItems(@Valid @RequestBody CreateItem itemData) {
        
        if (!StringUtils.isBlank(itemData.getSku()) && service.findByItemCode(itemData.getSku()).isPresent()) {
            throw APIException.conflict("Item with code {0} already exists.", itemData.getSku());
        }

        ItemData result = service.createItem(itemData);

        Pager<ItemData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Item created successful");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Inventory", "Created an item "+result.getItemName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/items/{code}")
    @PreAuthorize("hasAuthority('view_items')")
    public ItemData getItem(@PathVariable(value = "code") String code) {
        Item item = service.findByItemCode(code)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", code));
        auditTrailService.saveAuditTrail("Inventory", "Viewed item "+item.getItemName());
        return item.toData();
    }

    @GetMapping("/items/{id}/details")
    @PreAuthorize("hasAuthority('view_items')")
    public ResponseEntity<?> getItem(@PathVariable(value = "id") Long id) {
        Item item = service.findById(id)
                .orElseThrow(() -> APIException.notFound("Item with Id {0} not found.", id));
        auditTrailService.saveAuditTrail("Inventory", "Viewed item "+item.getItemName());
        return ResponseEntity.ok(service.toItemData(item));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAuthority('edit_items')")
    public ResponseEntity<?> updateItems(@PathVariable(value = "id") Long id, @Valid @RequestBody CreateItem itemData) {

        Item result = service.updateItem(id, itemData);

        Pager<ItemData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Item update successful");
        pagers.setContent(result.toData());
        auditTrailService.saveAuditTrail("Inventory", "Edited item "+result.getItemName());
        return ResponseEntity.ok(pagers);

    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('view_items')")
    public ResponseEntity<?> getAllItems(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "category", required = false) final ItemCategory category,
            @RequestParam(value = "type", required = false) final ItemType type,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ItemData> list = service.fetchItems(category, type, includeClosed, term, pageable).map(u -> u.toData());

        Pager<List<ItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Items");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Inventory", "Viewed all registered items ");
        return ResponseEntity.ok(pagers);
    }

    //generate a single api that returns all the setup required for this object
    @GetMapping("/items/$metadata")
    @PreAuthorize("hasAuthority('view_items')")
    public ResponseEntity<?> getItemMetadata() {
        ItemMetadata metadata = service.getItemMetadata();
        return ResponseEntity.ok(metadata);
    }
    @PutMapping("/items/reorder-level")
    @PreAuthorize("hasAuthority('edit_items')")
    public ResponseEntity<Pager<ReorderLevelData>> updateReorderLevel(@Valid @RequestBody ReorderLevelData reorderLevelData) {
        ReorderRule rule = service.createReorderLevel(reorderLevelData);
        Pager<ReorderLevelData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Item Reorder Rule Updated Successful");
        pagers.setContent(rule.toData());
        return ResponseEntity.ok(pagers);

    }
}

package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.stock.inventory.data.*;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequiredArgsConstructor
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService service;
    private final AuditTrailService auditTrailService;

    @PostMapping("/inventory-entries")
    @PreAuthorize("hasAuthority('create_inventory')")
    public ResponseEntity<?> createStockEntry(@Valid @RequestBody CreateStockEntry stocks) {

        String result = service.createStockEntry(stocks);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Movement successful");
        pagers.setContent(new TransData(result));
        auditTrailService.saveAuditTrail("Inventory", "Created an inventory stock entry");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PostMapping("/inventory-entries/supplier")
    @PreAuthorize("hasAuthority('create_inventory')")
    public ResponseEntity<?> createStockEntrySupplier(@Valid @RequestBody SupplierStockEntry stocks) {

        String result = service.receiveSupplierStocks(stocks);

        Pager<TransData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Movement successful");
        pagers.setContent(new TransData(result));
        auditTrailService.saveAuditTrail("Inventory", "Created an inventory stock entry  for a specified supplier");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    //inventories/{id}
    @GetMapping("/inventory-entries/{id}")
    @PreAuthorize("hasAuthority('view_inventory')")
    public StockEntryData searchStockEntry(@PathVariable(value = "id") Long id) {
        StockEntryData stocks = service.getStockEntry(id).toData();
        auditTrailService.saveAuditTrail("Inventory", "Viewed an inventory stock entry with id " + id);
        return stocks;
    }

    @GetMapping("/inventory-entries")
    @PreAuthorize("hasAuthority('view_inventory')")
    public ResponseEntity<?> getAllStockEntries(
            @RequestParam(value = "store_id", required = false) final Long storeId,
            @RequestParam(value = "item_id", required = false) final Long itemId,
            @RequestParam(value = "reference", required = false) final String referenceNumber,
            @RequestParam(value = "transactionId", required = false) final String transactionId,
            @RequestParam(value = "deliveryNo", required = false) final String deliveryNumber,
            @RequestParam(value = "purpose", required = false) final MovementPurpose purpose,
            @RequestParam(value = "type", required = false) MovementType type,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<StockEntryData> list = service
                .getStockEntries(storeId, itemId, referenceNumber, transactionId, deliveryNumber, purpose, type, range, pageable)
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
        auditTrailService.saveAuditTrail("Inventory", "Viewed all inventory stock entries");
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/inventory-entries/{item_id}/drug-flow")
    @PreAuthorize("hasAuthority('view_inventory')")
    public ResponseEntity<?> getDrugflow(
            @PathVariable(value = "item_id") final Long itemId,
            @RequestParam(value = "store_id", required = false) final Long storeId,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        List<StockMovement> list = service.getStockMovement(storeId, itemId, range);
        auditTrailService.saveAuditTrail("Inventory", "Viewed inventory stock entries for item identified by item id " + itemId);
        Pager<?> pagers = PaginationUtil.paginateList(list, "Item Flow Report", "", pageable);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/inventory-entries/stock-transfers")
    public ResponseEntity<Pager<StockEntryData>> getStockTransfers(
            @RequestParam(value = "store_id", required = false) final Long storeId,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "status", required = false) List<StockEntry.Status> status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<StockTransferData> list = service.getStockTransfers(storeId, range, status, pageable);
        Pager<StockEntryData> pager = (Pager<StockEntryData>) PaginationUtil.toPager(list, "Stock Transfers");
        return ResponseEntity.ok(pager);
    }

    @PatchMapping("/inventory-entries/stock-transfers/{transferNo}")
    public ResponseEntity<StockTransferData> updateStockTransfers(@PathVariable(value = "transferNo") String transferNo) {
        StockTransferData data = service.receiveStockTransfer(transferNo);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/inventory-entries/stock-transfers/{transferNo}")
    public ResponseEntity<List<StockEntryData>> getStockTransferItems(@PathVariable(value = "transferNo") String transferNo) {
        List<StockEntryData> data = service.getStockTransferItems(transferNo)
                .stream().map(StockEntry::toData).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

}

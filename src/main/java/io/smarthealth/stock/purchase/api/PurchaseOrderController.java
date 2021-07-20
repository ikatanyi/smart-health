package io.smarthealth.stock.purchase.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.data.PurchaseOrderItemData;
import io.smarthealth.stock.purchase.domain.HtmlData;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.purchase.service.PurchaseService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Api
@RestController
@RequestMapping("/api")
public class PurchaseOrderController {

    private final PurchaseService service;
    private final AuditTrailService auditTrailService;

    public PurchaseOrderController(PurchaseService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/purchaseorders")
    @PreAuthorize("hasAuthority('create_purchaseorders')")
    public ResponseEntity<?> createPurchaseOrder(@Valid @RequestBody PurchaseOrderData orderData) {

        PurchaseOrderData result = service.createPurchaseOrder(orderData);

        Pager<PurchaseOrderData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Order created successful");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Purchase", "Created a purchase order "+result.getOrderNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/purchaseorders/{orderNo}")
    @PreAuthorize("hasAuthority('view_purchaseorders')")
    public PurchaseOrderData getPurchaseOrder(@PathVariable(value = "orderNo") String code) {
        auditTrailService.saveAuditTrail("Purchase", "Viewed a purchase order with orderNo "+code);
        return service.findByOrderNumberOrThrow(code).toData();
    }

    @GetMapping("/purchaseorders/{id}/html")
    @PreAuthorize("hasAuthority('view_purchaseorders')")
    public ResponseEntity<?> getPurchaseOrderHtml(@PathVariable(value = "id") String code) {
        HtmlData data = service.purchaseOrderHtml(code);
        auditTrailService.saveAuditTrail("Purchase", "Viewed a purchase order identified by id "+code);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/purchaseorders")
    @PreAuthorize("hasAuthority('view_purchaseorders')")
    public ResponseEntity<?> getAllPurchaseOrders(
            @RequestParam(value = "showItems", required = false) boolean showItems,
            @RequestParam(value = "supplierId", required = false) Long supplierId,
            @RequestParam(value = "status", required = false) List<PurchaseOrderStatus> status,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size, Sort.by("id").descending());
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<PurchaseOrderData> list = service.getPurchaseOrders(supplierId, status, search, range, pageable)
                .map(u -> u.toData());

        Pager<List<PurchaseOrderData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Purchase Orders");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Purchase", "Viewed all purchase orders ");
        return ResponseEntity.ok(pagers);
    }
    
    @PutMapping("/purchaseorders/{id}")
    @PreAuthorize("hasAuthority('create_purchaseorders')")
    public PurchaseOrderData updatePurchaseOrder(@PathVariable(value = "id") Long code, @Valid @RequestBody PurchaseOrderData orderData) {
        auditTrailService.saveAuditTrail("Purchase", "Edited a purchase order identified by "+code);
        return service.updatePurchaseOrder(code,orderData).toData();
    }
    
    @DeleteMapping("/purchaseorders-item/{id}")
    @PreAuthorize("hasAuthority('create_purchaseorders')")
    public ResponseEntity<?> removePurchaseOrderItem(@PathVariable(value = "id") Long id) {
         service.removePurchaseOrderItem(id);
         auditTrailService.saveAuditTrail("Purchase", "Deleted a purchase order identified by "+id);
         return ResponseEntity.accepted().build();
    }
    
    @PutMapping("/purchaseorders/{id}/cancel")
    @PreAuthorize("hasAuthority('create_purchaseorders')")
    public ResponseEntity<?> removePurchaseOrder(@PathVariable(value = "id") Long id, @RequestParam(value = "remarks", required = false) String remarks) {
         service.cancelPurchaseOrder(id,remarks);
         auditTrailService.saveAuditTrail("Purchase", "Deleted purchase order identified by "+id);
         return ResponseEntity.accepted().build();
    }
    
    @PostMapping("/purchaseorders/{id}/order-item")
    @PreAuthorize("hasAuthority('create_purchaseorders')")
    public ResponseEntity<?> addPurchaseOrderItem(@PathVariable(value = "id") Long id, @Valid @RequestBody PurchaseOrderItemData orderData) {

        PurchaseOrderItemData result = PurchaseOrderItemData.map(service.addPurchaseOrderItem(id,orderData));

        Pager<PurchaseOrderItemData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Order created successful");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Purchase", "An item identified by " +id+" added to purchase order "+result.getOrderNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PutMapping("/purchaseorders/{orderNo}/authorise")
    public ResponseEntity<PurchaseOrderData> approvePurchaseOrder(@PathVariable(value = "orderNo") String orderNo) {
        PurchaseOrder order = service.approveOrder(orderNo);
        return ResponseEntity.ok(order.toData());
    }
}

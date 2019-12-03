package io.smarthealth.stock.purchase.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.service.PurchaseService; 
import io.swagger.annotations.Api;
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
public class PurchaseRestController {

    private final PurchaseService service;

    public PurchaseRestController(PurchaseService service) {
        this.service = service;
    }

    @PostMapping("/purchaseorders")
    public ResponseEntity<?> createPurchaseOrder(@Valid @RequestBody PurchaseOrderData orderData) {

        PurchaseOrderData result = service.createPurchaseOrder(orderData);

        Pager<PurchaseOrderData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Order created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/purchaseorders/{id}")
    public PurchaseOrderData getPurchaseOrder(@PathVariable(value = "id") Long code) {
        PurchaseOrder po = service.findOneWithNoFoundDetection(code);
        return PurchaseOrderData.map(po);
    }

    @GetMapping("/purchaseorders")
    public ResponseEntity<?> getAllPurchaseOrders(  
             @RequestParam(value = "showItems", required = false) boolean  showItems,
            @RequestParam(value = "status", required = false) final String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PurchaseOrderData> list = service.getPurchaseOrders(status, pageable)
                .map(u -> PurchaseOrderData.map(u));

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

        return ResponseEntity.ok(pagers);
    }
}

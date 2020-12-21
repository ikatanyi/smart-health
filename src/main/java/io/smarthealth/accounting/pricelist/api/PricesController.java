package io.smarthealth.accounting.pricelist.api;

import io.smarthealth.accounting.pricelist.data.PriceListData;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class PricesController {

    private final PricelistService priceService;
    private final AuditTrailService auditTrailService;

    public PricesController(PricelistService priceService, AuditTrailService auditTrailService) {
        this.priceService = priceService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/pricelists")
    @PreAuthorize("hasAuthority('create_pricelist')")
    public ResponseEntity<?> createPriceList(@Valid @RequestBody PriceListData data) {

        PriceList item = priceService.createPriceList(data);

        Pager<PriceListData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricelist Created.");
        pagers.setContent(item.toData());
        auditTrailService.saveAuditTrail("PriceList", "Created a pricelist item "+item.getItem().getItemName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/pricelists/bulk")
    @PreAuthorize("hasAuthority('create_pricelist')")
    public ResponseEntity<?> createPriceList(@Valid @RequestBody List<PriceListData> data) {

        List<PriceListData> item = priceService.createPriceList(data)
                .stream()
                .map(x -> {
                   auditTrailService.saveAuditTrail("PriceList", "Created a pricelist item"+x.getItem().getItemName());
                   return x.toData();
                })
                .collect(Collectors.toList());

        Pager< List<PriceListData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricelist Created.");
        pagers.setContent(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/pricelists/{id}")
    @PreAuthorize("hasAuthority('view_pricelist')")
    public ResponseEntity<?> getPriceList(@PathVariable(value = "id") Long id) {
        PriceList item = priceService.getPriceList(id);
        auditTrailService.saveAuditTrail("PriceList", "Searched a pricelist item identified by "+id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/pricelists/{id}")
    @PreAuthorize("hasAuthority('edit_pricelist')")
    public ResponseEntity<?> updatePriceList(@PathVariable(value = "id") Long id, @Valid @RequestBody PriceListData data) {
        PriceList item = priceService.updatePriceList(id, data);
        auditTrailService.saveAuditTrail("PriceList", "Edited a pricelist item identified by "+id);
        return ResponseEntity.ok(item.toData());
    }

    @DeleteMapping("/pricelists/{id}")
    @PreAuthorize("hasAuthority('delete_pricelist')")
    public ResponseEntity<?> deletePriceList(@PathVariable(value = "id") Long id) {
        priceService.deletePriceList(id);
        auditTrailService.saveAuditTrail("PriceList", "Deleted a pricelist item identified by "+id);
        return ResponseEntity.accepted().build();
    }
//String queryItem, Long servicePointId, Boolean defaultPrice

    @GetMapping("/pricelists")
    @PreAuthorize("hasAuthority('view_pricelist')")
    public ResponseEntity<?> getPriceLists(
            @RequestParam(value = "queryItem", required = false) String queryItem,
            @RequestParam(value = "servicePointId", required = false) Long servicePointId,
            @RequestParam(value = "is_default_price", required = false) Boolean defaultPrice,
            @RequestParam(value = "item_category", required = false) List<ItemCategory> itemCategory,
            @RequestParam(value = "item_type", required = false) ItemType itemType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PriceListData> list = priceService.getPriceLists(queryItem, servicePointId, defaultPrice, itemCategory, itemType, pageable)
                .map(x -> {
                    auditTrailService.saveAuditTrail("PriceList", "Viewed pricelist item "+x.getItem().getItemName());
                    return x.toData();
                });

        Pager<List<PriceListData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Product & Service Pricelist");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/pricelists/location/{servicepointId}")
    @PreAuthorize("hasAuthority('view_pricelist')")
    public ResponseEntity<?> getPriceListByLocation(
            @PathVariable(value = "servicepointId") Long servicePointId,
            @RequestParam(value = "pricebook_id", required = false) Long priceBookId,
            @RequestParam(value = "item_id", required = false) Long itemId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PriceListData> list = priceService.getPricelistByLocation(servicePointId, priceBookId,itemId, pageable)
                .map(x -> {
                    auditTrailService.saveAuditTrail("PriceList", "Viewed pricelist item "+x.getItem().getItemName());
                    return x.toData();
                        });

        Pager<List<PriceListData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Product & Service Pricelist");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
   
    @GetMapping("/pricelists/search")
    @PreAuthorize("hasAuthority('view_pricelist')")
    public ResponseEntity<?> getPriceListByLocation(
            @RequestParam(value = "item") String item,
            @RequestParam(value = "pricebook_id", required = false) Long priceBookId,
            @RequestParam(value = "scheme_id", required = false) Long schemeId,
            @RequestParam(value = "servicepoint_id", required = false) Long servicePointId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PriceListData> list = priceService.searchPriceList(item, servicePointId, priceBookId, pageable)
                .map(x -> {
                    auditTrailService.saveAuditTrail("PriceList", "Viewed pricelist item "+x.getItem().getItemName());
                    return x.toData();                    
                        });

        Pager<List<PriceListData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Product & Service Pricelist");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}

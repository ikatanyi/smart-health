package io.smarthealth.accounting.pricelist.api;

import io.smarthealth.accounting.pricelist.data.PriceBookData;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceType;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.PriceBookItemData;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.data.ItemSimpleData;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.smarthealth.accounting.pricelist.data.BulkPriceUpdate;

/**
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class PricebookRestController {

    private final PricebookService service;
    private final AuditTrailService auditTrailService;

    public PricebookRestController(PricebookService pricebookService, AuditTrailService auditTrailService) {
        this.service = pricebookService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/pricebooks")
    @PreAuthorize("hasAuthority('create_pricebook')")
    public ResponseEntity<?> createPricebook(@Valid @RequestBody PriceBookData priceBookData) {
        if (service.getPricebookByName(priceBookData.getName()).isPresent()) {
            throw APIException.conflict("Price Book with name {0} already exists.", priceBookData.getName());
        }

        PriceBookData result = service.createPricebook(priceBookData);
        auditTrailService.saveAuditTrail("PriceBook", "Created Price book " + result.getName());
        Pager<PriceBookData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricebook created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PutMapping("/pricebooks/{id}")
    @PreAuthorize("hasAuthority('edit_pricebook')")
    public ResponseEntity<?> updatePricebook(@PathVariable(value = "id") Long id, @Valid @RequestBody PriceBookData priceBookData) {
//        if (service.getPricebookByName(priceBookData.getName()).isPresent()) {
//            throw APIException.conflict("Price Book with name {0} already exists.", priceBookData.getName());
//        }

        PriceBookData result = service.updatePricebook(id, priceBookData);
        auditTrailService.saveAuditTrail("PriceBook", "Edited Price book " + result.getName());
        Pager<PriceBookData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricebook updated successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/pricebooks/{id}")
    @PreAuthorize("hasAuthority('view_pricebook')")
    public PriceBookData getPricebook(@PathVariable(value = "id") Long code) {
        PriceBook pricebook = service.getPricebook(code)
                .orElseThrow(() -> APIException.notFound("Price Book with id {0} not found.", code));
        auditTrailService.saveAuditTrail("PriceBook", "Searched Price book Identified by " + code);
        return PriceBookData.map(pricebook);
    }

    @GetMapping("/pricebooks")
    @PreAuthorize("hasAuthority('view_pricebook')")
    public ResponseEntity<?> getAllPricebook(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "type", required = false) final PriceType type,
            @RequestParam(value = "category", required = false) final PriceCategory category,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PriceBookData> list = service.getPricebooks(category, type, pageable, includeClosed).map(u -> PriceBookData.map(u));
        auditTrailService.saveAuditTrail("PriceBook", "Viewed Price books");
        Pager<List<PriceBookData>> pagers = new Pager();
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

    @GetMapping("/pricebooks/{id}/items")
//    @PreAuthorize("hasAuthority('edit_pricebook')")
    public ResponseEntity<?> getPricebookItem(@PathVariable(value = "id") Long id,
                                              @RequestParam(value = "q", required = false) final String term,
                                              @RequestParam(value = "page", required = false) Integer page,
                                              @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        auditTrailService.saveAuditTrail("PriceBook", "Viewed Price book items");
        Pageable pageable = PaginationUtil.createPage(page, size);
        Pager<PriceBookItemData> result = service.getPriceBookItems(id, term, pageable);
        return ResponseEntity.ok(result);
    }
//
//    @GetMapping("/pricebooks/{bookId}/items/{itemId}")
////    @PreAuthorize("hasAuthority('view_pricebook')")
//    public ResponseEntity<?> getPricebookItem(
//            @PathVariable(value = "bookId") Long bookId,
//            @PathVariable(value = "itemId") Long itemId
//    ) {
//        Pager<PriceBookItemData> result = service.getPriceBookItems(id, term, pageable);
//        return ResponseEntity.ok(result);
//    }

    @PostMapping("/pricebooks/{id}/items")
//    @PreAuthorize("hasAuthority('edit_pricebook')")
    public ResponseEntity<?> addPricebookItem(@PathVariable(value = "id") Long id, @Valid @RequestBody ItemSimpleData pricebookItem) {
        service.addPriceBookItem(id, pricebookItem);
        auditTrailService.saveAuditTrail("PriceBook", "Added a Price book item " + pricebookItem.getItemName());
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Item Successfully addedd"));
    }

    @DeleteMapping("/pricebooks/{id}/items/{itemId}")
    @PreAuthorize("hasAuthority('edit_pricebook')")
    public ResponseEntity<?> deletePricebookItems(@PathVariable(value = "id") Long id, @PathVariable(value = "itemId") Long itemId) {
        service.deletePriceItem(id, itemId);
        auditTrailService.saveAuditTrail("PriceBook", "Deleted a Price book item identified by id " + itemId);
        Pager<PriceBookData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricebook updated successful");
        pagers.setContent(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PutMapping("/pricebooks/items/batch-update")
//    @PreAuthorize("hasAuthority('edit_pricebook')")
    public ResponseEntity<?> batchUpdatePricebookItem(@Valid @RequestBody BulkPriceUpdate bulks) {
        service.batchUpdatePriceItem(bulks);
        auditTrailService.saveAuditTrail("PriceBook", "Upadted Price book price items[Bulk]");
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Item Successfully Updated"));
    }
}

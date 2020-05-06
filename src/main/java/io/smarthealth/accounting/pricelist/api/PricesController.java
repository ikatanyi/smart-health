package io.smarthealth.accounting.pricelist.api;

import io.smarthealth.accounting.pricelist.data.PriceListData;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public PricesController(PricelistService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/pricelists")
    public ResponseEntity<?> createPriceList(@Valid @RequestBody PriceListData data) {

        PriceList item = priceService.createPriceList(data);

        Pager<PriceListData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricelist Created.");
        pagers.setContent(item.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/pricelists/bulk")
    public ResponseEntity<?> createPriceList(@Valid @RequestBody List<PriceListData> data) {

        List<PriceListData> item = priceService.createPriceList(data)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        Pager< List<PriceListData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricelist Created.");
        pagers.setContent(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/pricelists/{id}")
    public ResponseEntity<?> getPriceList(@PathVariable(value = "id") Long id) {
        PriceList item = priceService.getPriceList(id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/pricelists/{id}")
    public ResponseEntity<?> updatePriceList(@PathVariable(value = "id") Long id, @Valid @RequestBody PriceListData data) {
        PriceList item = priceService.updatePriceList(id, data);
        return ResponseEntity.ok(item.toData());
    }

    @DeleteMapping("/pricelists/{id}")
    public ResponseEntity<?> deletePriceList(@PathVariable(value = "id") Long id) {
        priceService.deletePriceList(id);
        return ResponseEntity.accepted().build();
    }
//String queryItem, Long servicePointId, Boolean defaultPrice

    @GetMapping("/pricelists")
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
                .map(x -> x.toData());

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
    public ResponseEntity<?> getPriceListByLocation(
            @PathVariable(value = "servicepointId") Long servicePointId,
            @RequestParam(value = "pricebook_id", required = false) Long priceBookId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PriceListData> list = priceService.getPricelistByLocation(servicePointId, priceBookId, pageable)
                .map(x -> x.toData());

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
    public ResponseEntity<?> getPriceListByLocation(
            @RequestParam(value = "item") String item,
            @RequestParam(value = "pricebook_id", required = false) Long priceBookId,
            @RequestParam(value = "servicepoint_id", required = false) Long servicePointId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<PriceListData> list = priceService.searchPriceList(item, servicePointId, priceBookId, pageable)
                .map(x -> x.toData());

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

package io.smarthealth.stock.inventory.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.inventory.data.StockMovementData;
import io.smarthealth.stock.inventory.service.StockService;
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
@RestController
@Slf4j
@Api
@RequestMapping("/api/v1")
public class StocksRestController {

    private final StockService service;

    public StocksRestController(StockService itemService) {
        this.service = itemService;
    }

    @PostMapping("/stocks")
    public ResponseEntity<?> createStocks(@Valid @RequestBody StockMovementData stocks) {

        Long result = service.saveStock(stocks);

        Pager<Long> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Stock Movement successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/stocks/{id}")
    public StockMovementData getStock(@PathVariable(value = "id") Long id) {
        StockMovementData stocks = StockMovementData.map(service.findOneWithNoFoundDetection(id));
        return stocks;
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getAllStocks(
            @RequestParam(value = "store", required = false) final String store,
            @RequestParam(value = "item", required = false) final String item,
            @RequestParam(value = "reference", required = false) final String referenceNumber,
            @RequestParam(value = "transactionId", required = false) final String transactionId,
            @RequestParam(value = "deliveryNo", required = false) final String deliveryNumber,
            @RequestParam(value = "purpose", required = false) final String purpose,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<StockMovementData> list = service
                .getStockMovements(store, item, referenceNumber, transactionId, deliveryNumber, purpose, type, range, pageable)
                .map(u -> StockMovementData.map(u));

        Pager<List<StockMovementData>> pagers = new Pager();
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

        return ResponseEntity.ok(pagers);
    }


}

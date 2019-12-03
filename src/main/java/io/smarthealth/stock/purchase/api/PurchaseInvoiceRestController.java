package io.smarthealth.stock.purchase.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
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
public class PurchaseInvoiceRestController {

    private final PurchaseInvoiceService service;

    public PurchaseInvoiceRestController(PurchaseInvoiceService service) {
        this.service = service;
    }

    @PostMapping("/purchaseinvoices")
    public ResponseEntity<?> createPurchaseInvoice(@Valid @RequestBody PurchaseInvoiceData orderData) {

        PurchaseInvoiceData result = service.createPurchaseInvoice(orderData);

        Pager<PurchaseInvoiceData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Purchase Invoice created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/purchaseinvoices/{id}")
    public PurchaseInvoiceData getPurchaseInvoice(@PathVariable(value = "id") Long code) {
        PurchaseInvoice po = service.findOneWithNoFoundDetection(code);
        return PurchaseInvoiceData.map(po);
    }

    @GetMapping("/purchaseinvoices")
    public ResponseEntity<?> getAllPurchaseInvoices(
            @RequestParam(value = "status", required = false) final String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PurchaseInvoiceData> list = service.getPurchaseInvoices(status, pageable)
                .map(u -> PurchaseInvoiceData.map(u));

        Pager<List<PurchaseInvoiceData>> pagers = new Pager();
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

package io.smarthealth.accounting.pricebook.api;

import io.smarthealth.accounting.pricebook.data.PriceBookData;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.service.PricebookService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
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
public class PricebookRestController {

    private final PricebookService service;

    public PricebookRestController(PricebookService pricebookService) {
        this.service = pricebookService;
    }

    @PostMapping("/pricebooks")
    public ResponseEntity<?> createPricebook(@Valid @RequestBody PriceBookData priceBookData) {
        if (service.getPricebookByName(priceBookData.getName()).isPresent()) {
            throw APIException.conflict("Price Book with name {0} already exists.", priceBookData.getName());
        }

        PriceBookData result = service.createPricebook(priceBookData);

        Pager<PriceBookData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Pricebook created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/pricebooks/{id}")
    public PriceBookData getPricebook(@PathVariable(value = "id") Long code) {
        PriceBook pricebook = service.getPricebook(code)
                .orElseThrow(() -> APIException.notFound("Price Book with id {0} not found.", code));
        return PriceBookData.map(pricebook);
    }

    @GetMapping("/pricebooks")
    public ResponseEntity<?> getAllPricebook(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "type", required = false) final String type,
            @RequestParam(value = "category", required = false) final String category,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<PriceBookData> list = service.getPricebooks(category, type, pageable,includeClosed).map(u -> PriceBookData.map(u));

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

}

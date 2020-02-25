package io.smarthealth.stock.item.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemDatas;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemMetadata;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class ItemRestController {

    private final ItemService service;

    public ItemRestController(ItemService itemService) {
        this.service = itemService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> createItems(@Valid @RequestBody CreateItem itemData) {
        if (itemData.getSku()!=null && service.findByItemCode(itemData.getSku()).isPresent()) {
            throw APIException.conflict("Item with code {0} already exists.", itemData.getSku());
        }

        ItemDatas result = service.createItem(itemData);

        Pager<ItemDatas> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Item created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/items/{code}")
    public ItemDatas getItem(@PathVariable(value = "code") String code) {
        Item item = service.findByItemCode(code)
                .orElseThrow(() -> APIException.notFound("Account {0} not found.", code));
        return ItemDatas.map(item);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "category", required = false) final String category,
            @RequestParam(value = "type", required = false) final String type,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ItemDatas> list = service.fetchItems(category, type, includeClosed, term, pageable).map(u -> ItemDatas.map(u));

        Pager<List<ItemDatas>> pagers = new Pager();
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

        return ResponseEntity.ok(pagers);
    }

    //generate a single api that returns all the setup required for this object
    @GetMapping("/items/$metadata")
    public ResponseEntity<?> getItemMetadata() {
        ItemMetadata metadata = service.getItemMetadata();
        return ResponseEntity.ok(metadata);
    }

}

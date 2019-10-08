package io.smarthealth.stock.item.api;

import io.smarthealth.accounting.account.data.AccountData;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.net.URI;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ItemRestController {
    private final ItemService service;

    public ItemRestController(ItemService itemService) {
        this.service = itemService;
    }
    
      @PostMapping("/inventory/item")
    public ResponseEntity<?> createItems(@Valid @RequestBody ItemData itemData) {
        if (service.findByItemCode(itemData.getItemCode()).isPresent()) {
            throw APIException.conflict("Item with code {0} already exists.", itemData.getItemCode());
        }

        Item result = service.createItem(itemData);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/inventory/item/{code}")
                .buildAndExpand(result.getItemCode()).toUri();

        return ResponseEntity.created(location).body(result);

    }
}

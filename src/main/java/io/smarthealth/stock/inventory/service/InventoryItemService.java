package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.exception.ApiError;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.inventory.domain.specification.InventoryItemSpecification;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.stock.inventory.domain.InventoryItemRepository;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class InventoryItemService {

//    https://www.devglan.com/spring-boot/spring-boot-jms-activemq-example
    private final ItemService itemService;
    private final StoreService storeService;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public void decrease(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.decrease(qty);
        inventoryItemRepository.save(balance);
    }

    @Transactional
    public void increase(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.increase(qty);
        inventoryItemRepository.save(balance);
    }

    @Transactional
    public InventoryItem createInventoryItem(InventoryItemData itemData) {
        Item item = itemService.findItemEntityOrThrow(itemData.getItemId());
        Store store = storeService.getStoreWithNoFoundDetection(itemData.getStoreId());
        InventoryItem inventory = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));
        return save(inventory);
    }

    @Transactional
    public InventoryItem save(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    @Transactional
    public void adjustment(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.setAvailableStock(qty);
        inventoryItemRepository.save(balance);
    }

    public Page<InventoryItemData> getInventoryItems(Long storeId, String item, Pageable page, boolean includeClosed) {
        Store store = storeService.getStoreWithNoFoundDetection(storeId);

        Specification<InventoryItem> spec = InventoryItemSpecification.createSpecification(store, item, includeClosed);
        Page<InventoryItemData> inventoryItems = inventoryItemRepository.findAll(spec, page).map(itm -> itm.toData());

        return inventoryItems;
    }

    public Optional<InventoryItem> getInventoryItem(Long inventoryId) {
        return inventoryItemRepository.findById(inventoryId);
    }

    public InventoryItem getInventoryItemOrThrow(Long itemId, Long storeId) {
        return getInventoryItem(storeId, itemId)
                .orElseThrow(() -> APIException.notFound("Item with given identifier can not be in given Store"));
    }

    public Optional<InventoryItem> getInventoryItem(Long storeId, Long itemId) {
        Item item = itemService.findItemEntityOrThrow(itemId);
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        return inventoryItemRepository.findByItemAndStore(item, store);
    }

    public Page<InventoryItemData> getInventoryItemByStore(Long storeId, Pageable page) {
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        return inventoryItemRepository.findByStore(store, page).map(d -> d.toData());
    }

}

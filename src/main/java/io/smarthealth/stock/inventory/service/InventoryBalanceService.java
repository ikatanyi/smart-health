package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.InventoryBalanceData;
import io.smarthealth.stock.inventory.domain.InventoryBalance;
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
import io.smarthealth.stock.inventory.domain.InventoryBalanceRepository;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class InventoryBalanceService {

//    https://www.devglan.com/spring-boot/spring-boot-jms-activemq-example
    private final ItemService itemService;
    private final StoreService storeService;
    private final InventoryBalanceRepository inventoryItemRepository;

    @Transactional
    public void decrease(Item item, Store store, double qty) {
        InventoryBalance balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryBalance.create(store, item));

        balance.decrease(qty);
        inventoryItemRepository.save(balance);
    }

    @Transactional
    public void increase(Item item, Store store, double qty) {
        InventoryBalance balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryBalance.create(store, item));

        balance.increase(qty);
        inventoryItemRepository.save(balance);
    }

    @Transactional
    public void adjustment(Item item, Store store, double qty) {
        InventoryBalance balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryBalance.create(store, item));

        balance.setAvailableStock(qty);
        inventoryItemRepository.save(balance);
    }

    public Page<InventoryBalanceData> getInventoryBalance(Long storeId, Long itemId, DateRange range, Pageable pageable) {
        Item item = itemService.findItemEntityOrThrow(itemId);
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        Specification<InventoryBalance> spec = InventoryItemSpecification.createSpecification(store, item, range);
        Page<InventoryBalanceData> inventoryItems = inventoryItemRepository.findAll(spec, pageable).map(itm -> itm.toData());

        return inventoryItems;
    }

    public Optional<InventoryBalance> getInventoryBalance(Item item, Store store) {
        return inventoryItemRepository.findByItemAndStore(item, store);
    }

    public Page<InventoryBalanceData> getInventoryBalanceByItem(Long itemId, Pageable page) {
        Item item = itemService.findItemEntityOrThrow(itemId);
        return inventoryItemRepository.findByItem(item, page).map(d -> d.toData());
    }

}

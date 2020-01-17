package io.smarthealth.stock.item.service;

import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.domain.TaxRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.UuidGenerator;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.service.InventoryEventSender;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.data.Uoms;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemMetadata;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.ReorderRule;
import io.smarthealth.stock.item.domain.ReorderRuleRepository;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.specification.ItemSpecification;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"items"})
public class ItemService {

    private final TaxRepository taxRepository;
    private final ItemRepository itemRepository;
    private final UomService uomService;
    private final StoreService storeService;
    private final ReorderRuleRepository reorderRuleRepository;
    private final StockEntryRepository stockEntryRepository;
    private final InventoryEventSender inventoryEventSender;

    @Transactional
    @CachePut
    public ItemData createItem(CreateItem createItem) {
        Item item = new Item();
        item.setActive(Boolean.TRUE);
        item.setCategory(createItem.getStockCategory());
        item.setCostRate(createItem.getPurchaseRate());
        item.setDescription(createItem.getDescription());
        item.setItemCode(createItem.getSku());
        item.setItemName(createItem.getItemName());
        item.setItemType(createItem.getItemType());
        item.setRate(createItem.getRate());
        //check the category
        if (createItem.getItemUnit() != null) {
            Optional<Uom> uom = uomService.findUomById(Long.valueOf(createItem.getItemUnit()));
            if (uom.isPresent()) {
                item.setUom(uom.get());
            }
        }
        if (createItem.getTaxId() != null) {
            Optional<Tax> tax = taxRepository.findById(createItem.getTaxId());
            if (tax.isPresent()) {
                item.setTax(tax.get());
            }
        }
        if (createItem.getStockCategory() != null && createItem.getStockCategory().equals("drug")) {
            item.setDrug(createItem.getStockCategory().equals("drug"));
            item.setDrugCategory(createItem.getDrugCategory());
            item.setDrugForm(createItem.getDoseForm());
            item.setRoute(createItem.getDrugRoute());
            item.setStrength(createItem.getDrugStrength());
        }

        Item savedItem = itemRepository.save(item);

        if (createItem.getItemType().equals("Inventory") && createItem.getInventoryStore() != null) {
            Store store = storeService.getStore(createItem.getInventoryStore())
                    .orElseThrow(() -> APIException.notFound("Store with Identifier {0} not found ", createItem.getInventoryStore()));

            if (createItem.getStockBalance() > 0) {
                log.info("Receiving the initial stock");
                String trxId = UuidGenerator.newUuid();
                //determine the stock
                StockEntry stock = new StockEntry();
                stock.setQuantity(Double.valueOf(createItem.getStockBalance()));
                stock.setItem(item);
                stock.setMoveType(MovementType.Opening_Balance);
                if (createItem.getStockRatePerUnit() != null) {
                    BigDecimal qty = BigDecimal.valueOf(createItem.getStockBalance());
                    BigDecimal amt = createItem.getStockRatePerUnit().multiply(qty);
                    stock.setAmount(amt);
                    stock.setPrice(createItem.getStockRatePerUnit());
                }

                stock.setPurpose(MovementPurpose.Receipt);
                stock.setReferenceNumber("0000");
                stock.setStore(store);
                stock.setTransactionDate(LocalDate.now());
                stock.setTransactionNumber(trxId);
                stock.setUnit(createItem.getItemUnit());
                saveStockEntry(stock);

            }
            if (createItem.getReorderLevel() > 0) {
                ReorderRule rule = new ReorderRule();
                rule.setStore(store);
                rule.setStockItem(savedItem);
                rule.setReorderLevel(createItem.getReorderLevel());
                rule.setReorderQty(createItem.getOrderQuantity());
                reorderRuleRepository.save(rule);

            }
        }

        return ItemData.map(savedItem);
    }

    public Optional<Item> findById(final Long itemId) {
        return itemRepository.findById(itemId);
    }

    public Optional<Item> findByItemCode(final String itemCode) {
        return itemRepository.findByItemCode(itemCode);
    }

    public Item findByItemCodeOrThrow(final String itemCode) {
        return findByItemCode(itemCode)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", itemCode));
    }

    public Page<Item> fetchItems(String category, String type, boolean includeClosed, String term, Pageable pageable) {
        Specification<Item> spec = ItemSpecification.createSpecification(category, type, includeClosed, term);
        Page<Item> items = itemRepository.findAll(spec, pageable);
        return items;
    }

    public Item findItemEntityOrThrow(Long id) {
        return this.itemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Item id {0} not found.", id));
    }

    public Item findItemWithNoFoundDetection(String code) {
        return this.itemRepository.findByItemCode(code)
                .orElseThrow(() -> APIException.notFound("Item id {0} not found.", code));
    }

    @Transactional(readOnly = true)
    @Cacheable
    public Collection<Item> findAll() {
        return itemRepository.findAll();
    }

    public void saveStockEntry(StockEntry entry) {
        StockEntry savedEntry = stockEntryRepository.save(entry);
        inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), savedEntry.getStore(), savedEntry.getItem(), savedEntry.getQuantity()));
    }

    //this should be cached
    public ItemMetadata getItemMetadata() {
        List<StoreData> stores = storeService.getAllStores();
        List<Tax> tax = taxRepository.findAll();
        List<Uoms> uoms = uomService.getAllUnitofMeasure();

        ItemMetadata data = new ItemMetadata();
        data.setCode("0");
        data.setMessage("success");
        data.setStores(stores);
        data.setTaxes(tax);
        data.setUom(uoms);
        return data;
    }

    private InventoryEvent.Type getEvent(MovementType type) {
        return type == MovementType.Dispensed ? InventoryEvent.Type.Decrease : InventoryEvent.Type.Increase;
    }
}

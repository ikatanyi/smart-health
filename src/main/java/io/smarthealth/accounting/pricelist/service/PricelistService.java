package io.smarthealth.accounting.pricelist.service;

import io.smarthealth.accounting.pricelist.data.PriceListData;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.domain.PriceBookItem;
import io.smarthealth.accounting.pricelist.domain.PriceBookRepository;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.domain.PriceListRepository;
import io.smarthealth.accounting.pricelist.domain.specification.PriceListSpecification;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.domain.ServicePointRepository;
import io.smarthealth.debtor.scheme.domain.SchemeExclusionRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class PricelistService {

    private final PriceListRepository repository;
    private final PriceBookRepository priceBookRepository;
    private final ItemRepository itemRepository;
    private final ServicePointRepository servicePointRepository;
    private final SchemeExclusionRepository schemeExclusionRepository;

    @Transactional
    public PriceList createPriceList(PriceListData data) {
        PriceList items = toPriceList(data);
        if (items.getItem().getCategory().equals(ItemCategory.DoctorFee) || items.getItem().getCategory().equals(ItemCategory.CoPay)) {
            //look if item already exists under pricelist
            List<PriceList> priceList = repository.findByItem(items.getItem());
            if (priceList.size() > 0) {
                throw APIException.conflict("Pricelist for the service {0} already exists ", items.getItem().getItemName());
            }

        }

        Optional<PriceList> priceList = repository.findByItemAndServicePoint(items.getItem(), items.getServicePoint());
        if (priceList.isPresent()) {
            throw APIException.conflict("Pricelist for the service {0} already exists in the selected service point ", items.getItem().getItemName());
        }

        return save(items);
    }

    @Transactional
    public List<PriceList> createPriceList(List<PriceListData> data) {
        List<PriceList> toSave = data
                .stream()
                .map(x -> toPriceList(x))
                .collect(Collectors.toList());
        return repository.saveAll(toSave);
    }

    private PriceList toPriceList(PriceListData data) {
        Item item = findItem(data.getItemCode());
        ServicePoint servicePoint = getServicePoint(data.getServicePointId());

        PriceList priceList = new PriceList();
        priceList.setActive(Boolean.TRUE);
        priceList.setDefaultPrice(data.getDefaultPrice());
        priceList.setEffectiveDate(data.getEffectiveDate());
        priceList.setItem(item);
        if (data.getDefaultPrice() != null && data.getDefaultPrice()) {
            priceList.setSellingRate(data.getSellingRate());
        } else {
            priceList.setSellingRate(BigDecimal.ZERO);
        }
        priceList.setServicePoint(servicePoint);

        return priceList;
    }

    public PriceList save(PriceList pricelist) {
        return repository.save(pricelist);
    }

    public List<PriceList> saveAll(List<PriceList> pricelists) {
        return repository.saveAll(pricelists);
    }

    public PriceList getPriceList(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Pricelist with ID {0} Not Found"));
    }

    @Transactional
    public PriceList updatePriceList(Long id, PriceListData data) {
        PriceList toUpdateItem = getPriceList(id);
        Item item = findItem(data.getItemCode());
        ServicePoint servicePoint = getServicePoint(data.getServicePointId());

        toUpdateItem.setActive(data.getActive() != null ? data.getActive() : Boolean.TRUE);
        toUpdateItem.setDefaultPrice(data.getDefaultPrice());
//        toUpdateItem.setEffectiveDate(data.getEffectiveDate());
//        toUpdateItem.setItem(item);
        toUpdateItem.setSellingRate(data.getDefaultPrice()?item.getRate(): data.getSellingRate());
        toUpdateItem.setServicePoint(servicePoint);
        return repository.save(toUpdateItem);
//        return save(toUpdateItem);
    }

    public Page<PriceList> getPriceLists(String queryItem, Long servicePointId, Boolean defaultPrice, List<ItemCategory> category, ItemType itemType, Pageable page) {
        Specification<PriceList> spec = PriceListSpecification.createSpecification(queryItem, servicePointId, defaultPrice, category, itemType);
        return repository.findAll(spec, page);
    }

    /**
     * Get PriceList by Location and optional filter of price book
     *
     * @param servicePointId
     * @param priceBookId
     * @param page
     * @return
     */
    public Page<PriceList> getPricelistByLocation(Long servicePointId, Long priceBookId, Long itemId, Pageable page) {
        ServicePoint servicePoint = getServicePoint(servicePointId);
        Page<PriceList> prices = null;
        if (itemId != null) {
            Item item = itemRepository.findById(itemId).orElse(null);
            if (item != null) {
                prices = repository.findByServicePointAndItem(servicePoint, item, page);
            } else {
                prices = repository.findByServicePoint(servicePoint, page);
            }
        } else {
            prices = repository.findByServicePoint(servicePoint, page);
        }

        if (priceBookId != null) {
            Optional<PriceBook> priceBook = priceBookRepository.findById(priceBookId);
            if (priceBook.isPresent()) {
                PriceBook book = priceBook.get();

                if (book.isGlobalRate()) {
                    return prices.map(pb -> book.toPriceBookRate(pb));
                } else {
                    prices.map(pbi -> {
                        PriceBookItem i = findPriceItem(book, pbi.getItem());
                        if (i != null) {

                            return i.toPriceBookItemRate(pbi);
                        }
                        return pbi;
                    });
                }
            }
        }
        return prices;
    }

    /**
     * Get PriceList by Item and optional filter by price book
     */
    public Page<PriceList> getPricelistByItem(String itemCode, Long priceBookId, Pageable page) {
        Item item = findItem(itemCode);
        Page<PriceList> prices = repository.findByItem(item, page);

        if (priceBookId != null) {
            Optional<PriceBook> priceBook = priceBookRepository.findById(priceBookId);
            if (priceBook.isPresent()) {
                PriceBook book = priceBook.get();
                if (book.isGlobalRate()) {
                    return prices.map(pb -> book.toPriceBookRate(pb));
                } else {
                    prices.map(pbi -> {
                        PriceBookItem i = findPriceItem(book, pbi.getItem());
                        if (i != null) {
                            return i.toPriceBookItemRate(pbi);
                        }
                        return pbi;
                    });
                }
            }
        }
        return prices;
    }

    public PriceList fetchPriceListByItemAndServicePoint(final Item item, final ServicePoint servicePoint) {
        return repository.findByItemAndServicePoint(item, servicePoint).orElseThrow(() -> APIException.notFound("Pricelist not found ", ""));
    }

    public PriceList fetchPriceListByItemCategory(final ItemCategory category) {
        return repository.getPriceListByItemCategory(category).orElseThrow(() -> APIException.notFound("Pricelist not found ", ""));
    }

    public void deletePriceList(Long id) {
        PriceList item = getPriceList(id);
        repository.delete(item);
    }

    private Item findItem(String itemCode) {
        return itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", itemCode));
    }

    private Item findItemBy(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Item with ID {0} not found.", id));
    }

    public ServicePoint getServicePoint(Long id) {
        return servicePointRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Service point with id {0} not found", id));
    }

    private PriceBookItem findPriceItem(PriceBook book, Item item) {
        return book.getPriceBookItems()
                .stream()
                .filter(x -> Objects.equals(x.getItem().getId(), item.getId()))
                .findAny()
                .orElse(null);
    }

    /**
     * Search Item PriceList (item, servicePointId, priceBookId, pageable
     *
     * @param searchItem
     * @param servicePointId
     * @param priceBookId
     * @param page
     * @return
     */
    public Page<PriceList> searchPriceList(String searchItem, Long servicePointId, Long priceBookId, Pageable page) {

        Specification<PriceList> searchSpec = PriceListSpecification.searchSpecification(searchItem, servicePointId);

        Page<PriceList> prices = repository.findAll(searchSpec, page);
        if (priceBookId != null) {
            Optional<PriceBook> priceBook = priceBookRepository.findById(priceBookId);
            if (priceBook.isPresent()) {
                PriceBook book = priceBook.get();
                if (book.isGlobalRate()) {
                    return prices.map(pb -> book.toPriceBookRate(pb));
                } else {
                    prices.map(pbi -> {
                        PriceBookItem i = findPriceItem(book, pbi.getItem());
                        if (i != null) {
                            return i.toPriceBookItemRate(pbi);
                        }
                        return pbi;
                    });
                }
            }
        }
        return prices;
    }

    public double fetchPriceAmountByItemAndPriceBook(final Item item, final PriceBook book) {
        if (book != null) {
            if (book.isGlobalRate()) {
                double perc = book.getPercentage() != null ? book.getPercentage() : 0;
                if (book.getIncrease()) {
                    return (item.getRate().doubleValue() * (100 + perc) / 100);
                } else {
                    return (item.getRate().doubleValue() * (100 - perc) / 100);
                }
            } else {
                PriceBookItem i = findPriceItem(book, item);
                if (i != null) {
                    return i.getAmount().doubleValue();
                } else {
                    return item.getRate().doubleValue();
                }
            }
        } else {
            return item.getRate().doubleValue();
        }

    }
}

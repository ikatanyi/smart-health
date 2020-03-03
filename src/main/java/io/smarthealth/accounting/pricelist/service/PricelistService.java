/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Transactional
    public PriceList createPriceList(PriceListData data) {
        PriceList items = toPriceList(data);
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
        if (data.getDefaultPrice()!=null && !data.getDefaultPrice()) {
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

    public PriceList updatePriceList(Long id, PriceListData data) {

        PriceList toUpdateItem = getPriceList(id);
        Item item = findItem(data.getItemCode());
        ServicePoint servicePoint = getServicePoint(data.getServicePointId());

        toUpdateItem.setActive(Boolean.TRUE);
        toUpdateItem.setDefaultPrice(data.getDefaultPrice());
        toUpdateItem.setEffectiveDate(data.getEffectiveDate());
        toUpdateItem.setItem(item);
        toUpdateItem.setSellingRate(BigDecimal.ZERO);
        toUpdateItem.setServicePoint(servicePoint);

        return save(toUpdateItem);
    }

    public Page<PriceList> getPriceLists(String queryItem, Long servicePointId, Boolean defaultPrice, ItemCategory category, ItemType itemType, Pageable page) {
        Specification<PriceList> spec = PriceListSpecification.createSpecification(queryItem, servicePointId, defaultPrice, category, itemType);
        return repository.findAll(spec, page);
    }

    public Page<PriceList> getPricelistByLocation(Long servicePointId, Long priceBookId, Pageable page) {
        ServicePoint servicePoint = getServicePoint(servicePointId);

        Page<PriceList> prices = repository.findByServicePoint(servicePoint, page);

        if (priceBookId != null) {
            Optional<PriceBook> priceBook = priceBookRepository.findById(priceBookId);
            if (priceBook.isPresent()) {
                PriceBook book = priceBook.get();
                if (book.isGlobalRate()) {
                    return prices.map(pb -> book.toPriceBookRate(pb));
                } else {
                    prices.map(pbi -> {
                        PriceBookItem i = findPriceItem(book, pbi.getItem());
                        System.err.println("finding i "+i);
                        if (i != null) {
                            return i.toPriceBookItemRate(pbi);
                        }
                        return pbi;
                    });
                }
            }
        }
        return repository.findByServicePoint(servicePoint, page);
    }

    public void deletePriceList(Long id) {
        PriceList item = getPriceList(id);
        repository.delete(item);
    }

    private Item findItem(String itemCode) {
        return itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", itemCode));
    }

    public ServicePoint getServicePoint(Long id) {
        return servicePointRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Service point with id {0} not found", id));
    }

    private PriceBookItem findPriceItem(PriceBook book, Item item) {
        return book.getPriceBookItems().stream().filter(x -> x.getItem().getId().equals(item.getId())).findAny().orElse(null);
    }
}

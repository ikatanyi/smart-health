package io.smarthealth.accounting.pricelist.service;

import com.google.common.collect.Sets;
import io.smarthealth.accounting.pricelist.data.PriceBookData;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.domain.PriceBookItem;
import io.smarthealth.accounting.pricelist.domain.PriceBookRepository;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceType;
import io.smarthealth.accounting.pricelist.domain.specification.PriceBookSpecification;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.domain.CurrencyRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.pricelist.domain.PriceListDTO;
import io.smarthealth.infrastructure.imports.data.PriceBookItemData;
import io.smarthealth.stock.item.data.ItemSimpleData;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Kelsas
 */
@Service
public class PricebookService {

    private final PriceBookRepository priceBookRepository;
    private final CurrencyRepository currencyRepository;
    private final ItemService itemService;

    public PricebookService(PriceBookRepository priceBookRepository, CurrencyRepository currencyRepository, ItemService itemService) {
        this.priceBookRepository = priceBookRepository;
        this.currencyRepository = currencyRepository;
        this.itemService = itemService;
    }

    @Transactional
    public PriceBookData createPricebook(PriceBookData priceBook) {
        PriceBook book = new PriceBook();
        if (priceBook.getCurrencyId() != null) {
            Optional<Currency> currency = currencyRepository.findById(priceBook.getCurrencyId());

            if (currency.isPresent()) {
                book.setCurrency(currency.get());
            }
        }
        book.setDecimalPlace(priceBook.getDecimalPlace());
        book.setDescription(priceBook.getDescription());
        book.setIncrease(priceBook.getIsIncrease());
        book.setName(priceBook.getName());
        book.setPercentage(priceBook.getPercentage());
        book.setPriceType(priceBook.getPriceType());
        book.setPriceCategory(priceBook.getPriceCategory());
        book.setActive(true);
//        book.setStatus(priceBook.isStatus() ? "active" : "inactive");

        if (priceBook.getPricebookItems() != null) {
//            Set<PriceBookItem> itemlist = new HashSet<>();
            List<PriceBookItem> itemlist = new ArrayList<>();
            
            priceBook.getPricebookItems()
                    .stream()
                    .forEach(x -> {
                        Item item = itemService.findById(x.getItemId()).get();
                        itemlist.add(new PriceBookItem(item, x.getAmount()));
                    });

            book.addPriceItems(itemlist);
        }

        PriceBook savedBook = priceBookRepository.save(book);
        return PriceBookData.map(savedBook);
    }

    @Transactional
    public PriceBookData updatePricebook(Long id, PriceBookData priceBook) {
        PriceBook book = getPricebookWithNotFoundExeption(id);
        if (priceBook.getCurrencyId() != null) {
            Optional<Currency> currency = currencyRepository.findById(priceBook.getCurrencyId());

            if (currency.isPresent()) {
                book.setCurrency(currency.get());
            }
        }
        book.setDecimalPlace(priceBook.getDecimalPlace());
        book.setDescription(priceBook.getDescription());
        book.setIncrease(priceBook.getIsIncrease());
        book.setName(priceBook.getName());
        book.setPercentage(priceBook.getPercentage());
        book.setPriceType(priceBook.getPriceType());
        book.setPriceCategory(priceBook.getPriceCategory());
        book.setActive(true);
//        book.setStatus(priceBook.isStatus() ? "active" : "inactive");

        if (priceBook.getPricebookItems() != null) {
//            Set<PriceBookItem> itemlist = new HashSet<>();
            List<PriceBookItem> itemlist = new ArrayList<>();
            priceBook.getPricebookItems()
                    .stream()
                    .forEach(x -> {
                        Item item = itemService.findById(x.getItemId()).get();
                        itemlist.add(new PriceBookItem(item, x.getAmount()));
                    });

            book.addPriceItems(itemlist);
        }

        PriceBook savedBook = priceBookRepository.save(book);
        return PriceBookData.map(savedBook);
    }
    public PriceBookItem addItem(Long id, ItemSimpleData item){
        // 
        return null;
    }

    public Optional<PriceBook> getPricebook(Long id) {
        return priceBookRepository.findById(id);
    }

    public PriceBook getPricebookWithNotFoundExeption(Long id) {
        return priceBookRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Price book identified by {0} is not available ", id));
    }

    public Optional<PriceBook> getPricebookByName(String name) {
        return priceBookRepository.findByName(name);
    }

    public PriceBook getPricebookByNameOrThrowError(String name) {
        return priceBookRepository.findByName(name).orElseThrow(() -> APIException.notFound("Price book with name {0} not found ", name));
    }

    public Page<PriceBook> getPricebooks(PriceCategory category, PriceType type, Pageable page, boolean includeClosed) {
//        PriceType priceType = null;
//        PriceCategory priceCategory = null;
//        if (category != null && EnumUtils.isValidEnum(PriceCategory.class, category)) {
//            priceCategory = PriceCategory.valueOf(category);
//        }
//        if (type != null && EnumUtils.isValidEnum(PriceType.class, type)) {
//            priceType = PriceType.valueOf(type);
//        }
        Specification<PriceBook> spec = PriceBookSpecification.createSpecification(category, type, includeClosed);
        return priceBookRepository.findAll(spec, page);
    }

    public List<PriceBookData> getPricebooks() {
        return priceBookRepository.findAll()
                .stream()
                .map(price -> PriceBookData.map(price))
                .collect(Collectors.toList());
    }

    public List<PriceBookData> getPricebooks(PriceCategory category) {
        return priceBookRepository.findByPriceCategory(category)
                .stream()
                .map(price -> PriceBookData.map(price))
                .collect(Collectors.toList());
    }

    //
    public List<PriceListDTO> getPricelist() {
        return priceBookRepository.getPriceLists();
    }

    public List<PriceListDTO> searchPricelistByItem(String item) {
        final String likeExpression = "%" + item + "%";
        return priceBookRepository.searchPriceListByItem(likeExpression);
    }

    @org.springframework.transaction.annotation.Transactional
    public void createPriceBookItem(List<PriceBookItemData> d) {

        for (PriceBookItemData data : d) {

            Item item = itemService.findByItemCodeOrThrow(data.getItemCode());
            PriceBook priceBook = getPricebookByNameOrThrowError(data.getPriceBookName());

            PriceBookItem priceBookItem = new PriceBookItem();
            priceBookItem.setAmount(data.getAmount());
            priceBookItem.setItem(item);
            priceBookItem.setPriceBook(priceBook);

//            priceBook.addPriceItems(Sets.newHashSet(priceBookItem));
//            priceBookRepository.save(priceBook);
            priceBookRepository.addPriceBookItem(data.getAmount(), priceBook.getId(), item.getId());
        }

    }
}

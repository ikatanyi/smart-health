package io.smarthealth.accounting.pricebook.service;

import io.smarthealth.accounting.pricebook.data.PriceBookData;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.domain.PriceBookRepository;
import io.smarthealth.accounting.pricebook.domain.specification.PriceBookSpecification;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.domain.CurrencyRepository;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
        Optional<Currency> currency = currencyRepository.findById(priceBook.getCurrencyId());

        if (currency.isPresent()) {
            book.setCurrency(currency.get());
        }

        book.setDecimalPlace(priceBook.getDecimalPlace());
        book.setDescription(priceBook.getDescription());
        book.setIncrease(priceBook.getIsIncrease());
        book.setName(priceBook.getName());
        book.setPercentage(priceBook.getPercentage());
        book.setPriceBookType(PriceBook.PriceBookType.valueOf(priceBook.getPriceBookType()));
        book.setActive(true);
//        book.setStatus(priceBook.isStatus() ? "active" : "inactive");

        if (priceBook.getPricebookItems() != null) {
            List<Item> itemlist = new ArrayList<>();
            priceBook.getPricebookItems()
                    .stream().map(
                            (pb) -> itemService.findById(pb.getItemId())
                    ).filter(
                            (item) -> (item.isPresent())
                    ).forEachOrdered(
                            (item) -> {
                                itemlist.add(item.get());
                            });
            book.setPriceBookItems(itemlist);
        }

        PriceBook savedBook = priceBookRepository.save(book);
        return PriceBookData.map(savedBook);
    }

    public Optional<PriceBook> getPricebook(Long id) {
        return priceBookRepository.findById(id);
    }

    public Optional<PriceBook> getPricebookByName(String name) {
        return priceBookRepository.findByName(name);
    }

    public Page<PriceBook> getPricebooks(String type, String bookType, Pageable page, boolean includeClosed) {
        PriceBook.PriceBookType searchBookType = null;
        PriceBook.Type searchType = null;
        if (type != null && EnumUtils.isValidEnum(PriceBook.Type.class, type)) {
            searchType = PriceBook.Type.valueOf(type);
        }
        if (bookType != null && EnumUtils.isValidEnum(PriceBook.PriceBookType.class, bookType)) {
            searchBookType = PriceBook.PriceBookType.valueOf(bookType);
        }
        Specification<PriceBook> spec = PriceBookSpecification.createSpecification(searchType, searchBookType, includeClosed);
        return priceBookRepository.findAll(spec, page);
    }

    public List<PriceBookData> getPricebooks() {
        return priceBookRepository.findAll()
                .stream()
                .map(price -> PriceBookData.map(price))
                .collect(Collectors.toList());
    }
    //
}

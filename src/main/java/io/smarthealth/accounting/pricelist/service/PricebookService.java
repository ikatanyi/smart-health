package io.smarthealth.accounting.pricelist.service;

import com.google.common.base.Objects;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.pricelist.domain.PriceListDTO;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.imports.data.PriceBookItemData;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.data.ItemSimpleData;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.pricelist.data.BulkPriceUpdate;

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

    @Transactional
    public void addPriceBookItem(Long id, ItemSimpleData item) {
        PriceBook book = getPricebookWithNotFoundExeption(id);
        Item toSaveItem = itemService.findByItemCodeOrThrow(item.getItemCode());

        PriceBookItem priceBookItem = book.getPriceBookItems()
                .stream()
                .filter(x -> Objects.equal(x.getItem().getId(), item.getItemId()))
                .findAny()
                .orElse(null);
        if (priceBookItem == null) {
            priceBookRepository.addPriceBookItem(item.getAmount(), book.getId(), toSaveItem.getId());
        } else {
            priceBookRepository.updateBookItem(item.getAmount(), book.getId(), priceBookItem.getItem().getId());
        }
    }

    @Transactional
    public void batchUpdatePriceItem(BulkPriceUpdate updateData) {

        for (long p : updateData.getPricebooks()) {
            Optional<PriceBook> book = getPricebook(p);
            if (book.isPresent()) {
                Item toSaveItem = itemService.findItemEntityOrThrow(updateData.getItemId());

                PriceBookItem priceBookItem = book.get().getPriceBookItems()
                        .stream()
                        .filter(x -> Objects.equal(x.getItem().getId(), updateData.getItemId()))
                        .findAny()
                        .orElse(null);
                if (priceBookItem == null) {
                    priceBookRepository.addPriceBookItem(updateData.getAmount(), book.get().getId(), toSaveItem.getId());
                } else {
                    priceBookRepository.updateBookItem(updateData.getAmount(), book.get().getId(), priceBookItem.getItem().getId());
                }
            }
        };
    }

    @Transactional
    public void deletePriceItem(Long id, Long itemId) {
        PriceBook book = getPricebookWithNotFoundExeption(id);
        priceBookRepository.deleteBookItem(book.getId(), itemId);
    }

    public Pager<PriceBookItemData> getPriceBookItems(Long priceBookId, String term, Pageable page) {

        PriceBook book = getPricebookWithNotFoundExeption(priceBookId);

        List<PriceBookItemData> list;

        if (term != null) {
            String queryTerm = term.toLowerCase();

            list = book.getPriceBookItems()
                    .stream()
                    .filter(p -> (p.getItem().getItemName().toLowerCase().contains(queryTerm)) || (p.getItem().getItemCode().toLowerCase().contains(queryTerm)))
                    .map(x -> x.toData())
                    .collect(Collectors.toList());
        } else {
            list = book.getPriceBookItems().stream()
                    .map(x -> x.toData())
                    .collect(Collectors.toList());
        }

        return (Pager<PriceBookItemData>) PaginationUtil.paginateList(list, "", "", page);
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

    @Transactional
    public void createPriceBookItem(List<PriceBookItemData> d) {

        d.forEach((data) -> {
            Item item = itemService.findByItemCodeOrThrow(data.getItemCode());
            PriceBook priceBook = getPricebookByNameOrThrowError(data.getPriceBookName());

            PriceBookItem priceBookItem = new PriceBookItem();
            priceBookItem.setAmount(data.getAmount());
            priceBookItem.setItem(item);
            priceBookItem.setPriceBook(priceBook);

//            priceBook.addPriceItems(Sets.newHashSet(priceBookItem));
//            priceBookRepository.save(priceBook);
            priceBookRepository.addPriceBookItem(data.getAmount(), priceBook.getId(), item.getId());
        });

    }

}

package io.smarthealth.accounting.pricelist.domain;

import io.smarthealth.accounting.pricelist.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricelist.domain.enumeration.PriceType;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class PriceBook extends Auditable {

    @Enumerated(EnumType.STRING)
    private PriceCategory priceCategory;

    private String name;
    private String description;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pricebook_currency_id"))
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    private Double percentage;

    @Column(name = "is_increase")
    private Boolean increase; //mark down or mark up

    private Double decimalPlace;

    @OneToMany(mappedBy = "priceBook", cascade = CascadeType.ALL)
    private Set<PriceBookItem> priceBookItems = new HashSet<>();

    private boolean active;

    public PriceBook() {
    }

    public PriceBook(String name, PriceBookItem... priceBookItems) {
        this.name = name;
        for (PriceBookItem priceBookItem : priceBookItems) {
            priceBookItem.setPriceBook(this);
        }
        this.priceBookItems = Stream.of(priceBookItems).collect(Collectors.toSet());
    }

    public void addPriceItems(Set<PriceBookItem> bookItems) {
        this.priceBookItems = bookItems;
        this.priceBookItems.forEach(x -> x.setPriceBook(this));
    }

    public void addPriceItem(PriceBookItem bookItem) {
        System.err.println("my id "+this.getId());
        bookItem.setPriceBook(this);
        this.priceBookItems.add(bookItem);
    }

    public Boolean isGlobalRate() {
        return this.priceType == PriceType.fixed_percentage;
    }

    public PriceList toPriceBookRate(PriceList priceList) {
        BigDecimal adjstRate = BigDecimal.valueOf(this.percentage).multiply(priceList.getSpecialRate()).divide(BigDecimal.valueOf(100D));
        System.err.println("Calculating special prices: " + adjstRate);
        BigDecimal newPrice = priceList.getSpecialRate();
        if (this.increase) {
            newPrice = newPrice.add(adjstRate);
            priceList.setHasPriceBook(Boolean.TRUE);
        } else {
            newPrice = newPrice.subtract(adjstRate);
            priceList.setHasPriceBook(Boolean.TRUE);
        }
        System.err.println("My new price as now " + newPrice);
        priceList.setPriceBookAmount(newPrice);
        return priceList;
    }

    @Override
    public String toString() {
        return "Price Book [id=" + getId() + ", name=" + name + "]";
    }
}

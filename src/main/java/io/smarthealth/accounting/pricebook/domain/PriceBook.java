package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.accounting.pricebook.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceType;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
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
        this.priceBookItems=bookItems;
        this.priceBookItems.forEach(x -> x.setPriceBook(this));
    }
     
}

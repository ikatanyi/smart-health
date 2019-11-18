package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.accounting.pricebook.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceType;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
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
    @ManyToMany
    @JoinTable(name = "pricebook_items", joinColumns = {
        @JoinColumn(name = "pricebook_id", referencedColumnName = "id", foreignKey = @ForeignKey(name="fk_pricebook_items_pricebook_id"))}, inverseJoinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id",foreignKey = @ForeignKey(name="fk_pricebook_items_pricebook_item_id"))})
    private List<Item> priceBookItems;

    private boolean active;
}

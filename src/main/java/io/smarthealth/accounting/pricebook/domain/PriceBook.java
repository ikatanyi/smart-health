package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    public enum Type {
        Sales,
        Purchases
    }

    public enum PriceBookType {
        fixed_percentage,
        per_item
    }
    @Enumerated(EnumType.STRING)
    private Type type;
    private String name;
    private String description;
    @OneToOne
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private PriceBookType priceBookType;
    private Double percentage;
    @Column(name = "is_increase")
    private Boolean increase; //mark down or mark up
    private Double decimalPlace;
    @ManyToMany
    @JoinTable(name = "item_pricebook", joinColumns = {
        @JoinColumn(name = "pricebook_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")})
    private List<Item> priceBookItems;

    private boolean active;
}

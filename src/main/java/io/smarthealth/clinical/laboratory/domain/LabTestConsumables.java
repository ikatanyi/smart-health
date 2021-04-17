package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class LabTestConsumables extends Auditable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_consumables_item_id"))
    private Item item;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_consumables_lab_register_id"))
    private LabRegister labRegister;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_consumables_store_id"))
    private Store store;

    private Double quantity;
    private String unitOfMeasure;
    private String type;//Other/Reagent
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.inpatient.setup.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.inpatient.setup.data.BedChargeData;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "hp_bed_charges")
public class BedCharge extends Auditable {

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_charge_bed_id"))
    @ManyToOne
    private Bed bed;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_charge_item_id"))
    private Item item;
    private BigDecimal rate;
    private Boolean active;

    public BedChargeData toData() {
        BedChargeData data = new BedChargeData();
        data.setId(this.getId());
        data.setActive(this.active);
        data.setBed(this.bed.getName());
        data.setBedId(this.bed.getId());
        data.setItem(this.item.getItemName());
        data.setItemId(this.item.getId());
        data.setItemCode(this.item.getItemCode());
        data.setRate(this.rate);
        return data;
    }
}

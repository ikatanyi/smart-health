/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.data.ChargeData;
import io.smarthealth.infrastructure.domain.Identifiable;
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
 * @author kent
 */
@Entity
@Data
@Table(name = "facility_bed_charge")
public class BedCharge extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_charge_bed_id"))
    private BedType bedType;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_charge_item_id"))
    private Item item;
    private BigDecimal rate;
    private Boolean active = Boolean.TRUE;
    private Boolean recurrent = Boolean.FALSE;

    public BedChargeData toData() {
        BedChargeData data = new BedChargeData();
        data.setActive(this.getActive());
        data.setId(this.getId());
        data.setRecurrent(this.getRecurrent());
        data.setRate(this.getRate());
        if(this.getBedType()!=null){
            data.setBedType(this.getBedType().getName());
        }
        if (this.getItem() != null) {
            data.setItem(this.getItem().getItemName());
            data.setItemCode(this.getItem().getItemCode());
            data.setItemId(this.getItem().getId());
        }

        return data;
    }
}

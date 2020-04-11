/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.inpatient.setup.data;

import io.smarthealth.inpatient.setup.domain.*;
import io.smarthealth.infrastructure.domain.Auditable;
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
@Data
public class BedChargeData {
    private Long id;
    private Long bedId;
    private String bed;
    private Long itemId;
    private String itemCode;
    private String item;
    private BigDecimal rate;
    private Boolean active;

}

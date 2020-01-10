/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Embeddable
public class PriceItemId implements Serializable {

    public PriceBook pricebookId;
    private Item itemId;

}

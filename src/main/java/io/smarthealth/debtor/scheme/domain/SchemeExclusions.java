/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
@Entity
public class SchemeExclusions extends Auditable {

    @ManyToOne
    private Scheme scheme;

    @ManyToOne
    private Item item;
}

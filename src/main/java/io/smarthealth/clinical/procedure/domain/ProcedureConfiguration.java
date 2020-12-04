/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.clinical.procedure.domain.enumeration.FeeCategory;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "procedure_configuration") 
public class ProcedureConfiguration extends Auditable {

    private boolean isPercentage;
    
    private BigDecimal valueAmount;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_service_service_id"))
    private Item procedure;
    
    private FeeCategory feeCategory;
}

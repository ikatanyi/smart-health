/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class ProcedureItemData {

    private Long requestItemId;
    private Long medicId;
    private String itemCode;
    private Double itemPrice;
    private Double quantity; 
    private PaymentMethod paymentMethod;
}

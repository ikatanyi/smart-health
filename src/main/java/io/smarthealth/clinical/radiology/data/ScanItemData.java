/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import java.math.BigDecimal;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class ScanItemData {
    //StaffNumber
    private Long requestItemId;
    private Long medicId;
    private String itemCode;
    private Double itemPrice;
    private Double quantity; 
    private PaymentMethod paymentMethod;
}

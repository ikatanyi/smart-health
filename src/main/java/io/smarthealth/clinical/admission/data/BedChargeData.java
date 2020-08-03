/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import java.math.BigDecimal;
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

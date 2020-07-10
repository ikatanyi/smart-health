/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.data;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class VoidBillItem {
  @NotNull(message = "Patient BIll Item to cancel is Required")
    private Long billItemId;
    private String reason;
}

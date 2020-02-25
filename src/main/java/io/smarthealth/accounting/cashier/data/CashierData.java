/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.cashier.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data 
public class CashierData implements Serializable {
    private Long id;
    @NotNull(message = "User is Required")
    private Long userId;
    private String username;
    private String user;
     @NotNull(message = "Cashpoint is Required")
    private Long cashPointId;
    private String cashPoint; 
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate startDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate endDate;
    private Boolean active = Boolean.TRUE;
}

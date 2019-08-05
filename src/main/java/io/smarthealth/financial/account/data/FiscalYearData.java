/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data 
public class FiscalYearData {
    private String name;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate startDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate endDate;
    private Boolean active;
}

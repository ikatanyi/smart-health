/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TheatreBill {

    private String visitNumber;
    private String patientNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private Long servicePointId;
    private String servicePoint;
    private String paymentMode="Insurance";
    private List<TheatreBillItem> items = new ArrayList<>();

    public Double getAmount() {
        return items.stream()
                .map(x -> x.getQuantity() * x.getPrice())
                .reduce(0D, (x, y) -> x + y);
    }
}

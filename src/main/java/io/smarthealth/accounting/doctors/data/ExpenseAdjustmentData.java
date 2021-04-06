package io.smarthealth.accounting.doctors.data;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
public class ExpenseAdjustmentData {
    @Enumerated(EnumType.STRING)
    private AdjustmentType adjustmentType; //WriteOff, Adjustment
    private BigDecimal amount;
    private String narration;

}

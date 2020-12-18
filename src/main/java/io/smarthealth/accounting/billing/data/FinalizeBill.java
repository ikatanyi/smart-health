package io.smarthealth.accounting.billing.data;

import io.smarthealth.accounting.payment.data.BilledItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class FinalizeBill {

    private LocalDate billingDate;
    private String visitNumber;
    private String patientNumber;
    private List<BilledItem> billItems = new ArrayList<>();

}

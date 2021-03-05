package io.smarthealth.accounting.billing.data;

import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class VisitPaymentDetail {
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private String memberName;
    private String memberNumber;
    private Double copayValue;
    private CoPayType copayType;
    private boolean hasCapitation;
    private BigDecimal capitationAmount;

    public boolean hasCopay(){
        return copayValue!=null && copayValue > 0;
    }

}

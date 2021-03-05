package io.smarthealth.accounting.billing.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitPaymentDto {

    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private Double copayValue;
    private String copayType;

    public VisitPaymentDto(Long payerId, String payerName, Long schemeId, String schemeName, double copayValue, String copayType) {
        this.payerId = payerId;
        this.payerName = payerName;
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.copayValue = copayValue;
        this.copayType = copayType;
    }
}

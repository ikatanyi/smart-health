package io.smarthealth.clinical.visit.domain;

import lombok.Data;

@Data
public class VisitPaymentDetails {
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
}

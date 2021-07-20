package io.smarthealth.accounting.billing.domain;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@EqualsAndHashCode
public class BillingQuery {
    private final String search;
    private final String patientNumber;
    private final VisitEnum.VisitType visitType;
    private final PaymentMethod paymentMethod;
    private final DateRange dateRange;
    private final Pageable pageable;
    private final String visitNumber;

    public BillingQuery(String search, String patientNumber, VisitEnum.VisitType visitType, PaymentMethod paymentMethod,String visitNumber, DateRange dateRange, Pageable pageable) {
        this.search = search;
        this.patientNumber = patientNumber;
        this.visitType = visitType;
        this.paymentMethod = paymentMethod;
        this.dateRange = dateRange;
        this.pageable = pageable;
        this.visitNumber = visitNumber;
    }
}

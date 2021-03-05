package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.VisitBillSummary;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VisitBillSummaryRepository {

    Page<VisitBillSummary> getVisitBill(BillingQuery query);
}

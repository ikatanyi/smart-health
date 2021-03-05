package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.PatientBillDetail;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.DateRange;

import java.util.List;

public interface CustomPatientBillRepository {

    List<PatientBillDetail> getPatientBills(String search, String patientNumber, String visitNumber, PaymentMethod paymentMethod, Long payerId, Long schemeId, VisitEnum.VisitType visitType, DateRange range);
}

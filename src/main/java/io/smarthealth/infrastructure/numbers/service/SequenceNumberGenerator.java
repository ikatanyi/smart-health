package io.smarthealth.infrastructure.numbers.service;

import io.smarthealth.accounting.acc.domain.JournalEntryEntity;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.clinical.lab.domain.LabRegister;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;

/**
 *
 * @author Kelsas
 */
public interface SequenceNumberGenerator {

    public String generateTransactionNumber();

    public String generate(Patient patient);

    public String generate(Payment payment);

    public String generate(Visit visit);

    public String generate(PatientBill bill);

    public String generate(Invoice invoice);

    public String generate(Appointment appointment);

    public String generate(DoctorRequest request);

    public String generate(JournalEntryEntity journal);

    public String generate(LabRegister lab);

    public String generate(PurchaseOrder order);
}

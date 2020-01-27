package io.smarthealth.infrastructure.numbers.service.impl;

import io.smarthealth.accounting.acc.domain.JournalEntryEntity;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.clinical.lab.domain.LabRegister;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.numbers.domain.EntitySequenceType;
import io.smarthealth.infrastructure.numbers.domain.SequenceNumberFormat;
import io.smarthealth.infrastructure.numbers.domain.SequenceNumberFormatRepository;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import io.smarthealth.infrastructure.utility.Snowflake;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class SequenceNumberGeneratorImpl implements SequenceNumberGenerator {

    private final SequenceNumberFormatRepository repository;
    private final Snowflake snowflake;
    private int maxLength = 9;

    private String nextVal(Long number, EntitySequenceType type) {
        Optional<SequenceNumberFormat> format = repository.findBySequenceType(type);

        String nextVal = String.valueOf(number);

        if (format.isPresent()) {
            SequenceNumberFormat id = format.get();

            if (id.getMaxLength() > 0) {
                nextVal = StringUtils.leftPad(nextVal, id.getMaxLength(), '0');
            }

            String prefix = id.getPrefix();
            String suffix = id.getSuffix();

            if (prefix != null) {
                prefix = prefix.substring(0, Math.min(prefix.length(), 4));
                nextVal = prefix + nextVal;
            }

            if (suffix != null) {
                nextVal = nextVal + suffix;
            }
        } else {
            nextVal = StringUtils.leftPad(nextVal, maxLength, '0');
        }
        return nextVal;
    }

    @Override
    public String generateTransactionNumber() {
        return String.valueOf(snowflake.nextId());
    }

    @Override
    public String generate(Patient patient) {
        return nextVal(patient.getId(), EntitySequenceType.PATIENT);
    }

    @Override
    public String generate(Payment payment) {
        return nextVal(payment.getId(), EntitySequenceType.RECEIPT);
    }

    @Override
    public String generate(Visit visit) {
        return nextVal(visit.getId(), EntitySequenceType.VISIT);
    }

    @Override
    public String generate(PatientBill bill) {
        return nextVal(bill.getId(), EntitySequenceType.BILL);
    }

    @Override
    public String generate(Invoice invoice) {
        return nextVal(invoice.getId(), EntitySequenceType.INVOICE);
    }

    @Override
    public String generate(Appointment appointment) {
        return nextVal(appointment.getId(), EntitySequenceType.APPOINTMENT);
    }

    @Override
    public String generate(DoctorRequest request) {
        return nextVal(request.getId(), EntitySequenceType.REQUEST);
    }

    @Override
    public String generate(JournalEntryEntity journal) {
        return nextVal(journal.getId(), EntitySequenceType.JOURNAL);
    }

    @Override
    public String generate(LabRegister lab) {
        return nextVal(lab.getId(), EntitySequenceType.LABORATORY);
    }

}

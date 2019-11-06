package io.smarthealth.infrastructure.sequence;

/**
 * Global System Sequence generation
 *
 * @author Kelsas
 */
public enum SequenceType {
    PatientNumber("patient_number_seq"),
    VisitNumber("visit_number_seq"),
    JournalNumber("journal_seq"),
    DoctorRequestNumber("doctor_request_seq"),
    BillNumber("patient_bill_seq"),
    LabTestNumber("lab_test_seq"),
    PrescriptionNumber("prescription_seq");

    public final String sequenceName;

    private SequenceType(String sequenceName) {
        this.sequenceName = sequenceName;
    }

}

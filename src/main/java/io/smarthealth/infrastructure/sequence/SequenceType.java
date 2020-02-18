package io.smarthealth.infrastructure.sequence;

/**
 * Global System Sequence generation
 *
 * @author Kelsas
 */
@Deprecated
public enum SequenceType {
    PatientNumber("patient_number_seq"),
    VisitNumber("visit_number_seq"),
    JournalNumber("journal_seq"),
    DoctorRequestNumber("doctor_request_seq"),
    BillNumber("patient_bill_seq"),
    LabTestNumber("lab_test_seq"),
    PrescriptionNo("patient_prescription_seq"),
    ScanNumber("patient_scan_seq"),
    ProcedureNumber("patient_procedure_seq"),
    AppointmentTypeNumber("appointment_type_seq"),
    CreditNoteNumber("credit_note_seq"),
    RemittanceNumber("remitance_seq"),
    DispatchNumber("dispatch_seq");

    public final String sequenceName;

    public String getSequenceName() {
        return sequenceName;
    }

    private SequenceType(String sequenceName) {
        this.sequenceName = sequenceName;
    }

}

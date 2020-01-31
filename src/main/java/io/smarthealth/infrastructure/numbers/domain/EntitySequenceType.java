package io.smarthealth.infrastructure.numbers.domain;

import java.util.HashMap;
import java.util.Map;

public enum EntitySequenceType {

    PATIENT(1, "sequenceType.patient"),
    VISIT(2, "sequenceType.visit"),
    BILL(3, "sequenceType.bill"),
    INVOICE(4, "sequenceType.invoice"),
    RECEIPT(5, "sequenceType.receipt"),
    APPOINTMENT(6, "sequenceType.appointment"),
    REQUEST(7, "sequenceType.doctorrequest"),
    JOURNAL(8, "sequenceType.journal"),
    LABORATORY(9, "sequenceType.laboratory"),
    PRESCRITION(10, "sequenceType.prescription"),
    PURCHASEORDER(11, "sequenceType.purchaseOrder"),;

    private final Integer value;
    private final String code;

    private EntitySequenceType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, EntitySequenceType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;

    static {
        int i = 0;
        for (final EntitySequenceType type : EntitySequenceType.values()) {
            if (i == 0) {
                minValue = type.value;
            }
            intToEnumMap.put(type.value, type);
            if (minValue >= type.value) {
                minValue = type.value;
            }
            if (maxValue < type.value) {
                maxValue = type.value;
            }
            i = i + 1;
        }
    }

    public static EntitySequenceType fromInt(final int i) {
        final EntitySequenceType type = intToEnumMap.get(i);
        return type;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString();
    }

    public boolean isPatientNumber() {
        return this.value.equals(EntitySequenceType.PATIENT.getValue());
    }

    public boolean isVisitNumber() {
        return this.value.equals(EntitySequenceType.VISIT.getValue());
    }

    public boolean isBillNumber() {
        return this.value.equals(EntitySequenceType.BILL.getValue());
    }

    public Boolean isInvoiceNumber() {
        return this.value.equals(EntitySequenceType.INVOICE.getValue());
    }

    public Boolean isReceiptNumber() {
        return this.value.equals(EntitySequenceType.RECEIPT.getValue());
    }

    public boolean isAppointmentNumber() {
        return this.value.equals(EntitySequenceType.APPOINTMENT.getValue());
    }

    public boolean isRequestNumber() {
        return this.value.equals(EntitySequenceType.REQUEST.getValue());
    }

    public boolean isJournalNumber() {
        return this.value.equals(EntitySequenceType.JOURNAL.getValue());
    }

    public Boolean isLabNumber() {
        return this.value.equals(EntitySequenceType.LABORATORY.getValue());
    }

    public Boolean isPrescriptionNumber() {
        return this.value.equals(EntitySequenceType.PRESCRITION.getValue());
    }

}

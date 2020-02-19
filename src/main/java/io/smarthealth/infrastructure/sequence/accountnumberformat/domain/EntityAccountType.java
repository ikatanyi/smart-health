/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.sequence.accountnumberformat.domain;

import java.util.HashMap;
import java.util.Map;
@Deprecated
public enum EntityAccountType {
    PATIENT(1, "accountType.patient"),
    VISIT(2, "accountType.visit"),
    BILL(3, "accountType.bill"),
    INVOICE(4, "accountType.invoice"),
    RECEIPT(5, "accountType.receipt"),
    APPOINTMENT(6, "accountType.appointment");

    private final Integer value;
    private final String code;

    private EntityAccountType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, EntityAccountType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;

    static {
        int i = 0;
        for (final EntityAccountType type : EntityAccountType.values()) {
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

    public static EntityAccountType fromInt(final int i) {
        final EntityAccountType type = intToEnumMap.get(i);
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

    public boolean isPatientAccount() {
        return this.value.equals(EntityAccountType.PATIENT.getValue());
    }

    public boolean isVisitAccount() {
        return this.value.equals(EntityAccountType.VISIT.getValue());
    }

    public boolean isBillAccount() {
        return this.value.equals(EntityAccountType.BILL.getValue());
    }

    public Boolean isInvoiceAccount() {
        return this.value.equals(EntityAccountType.INVOICE.getValue());
    }

    public Boolean isReceiptAccount() {
        return this.value.equals(EntityAccountType.RECEIPT.getValue());
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.sequence.accountnumberformat.domain;

import io.smarthealth.infrastructure.sequence.accountnumberformat.data.EnumOptionData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kelsas
 */
@Deprecated
public class AccountNumberFormatEnumerations {

    public final static Set<AccountNumberPrefixType> accountNumberPrefixesForPatientAccounts = new HashSet<>(Arrays.asList(
            AccountNumberPrefixType.FACILITY_NAME, AccountNumberPrefixType.PATIENT_TYPE));
    public final static Set<AccountNumberPrefixType> accountNumberPrefixesForInvoiceAccounts = new HashSet<>(Arrays.asList(
            AccountNumberPrefixType.FACILITY_NAME, AccountNumberPrefixType.INVOICE_SHORT_NAME));
    public final static Set<AccountNumberPrefixType> accountNumberPrefixesForReceiptAccounts = new HashSet<>(Arrays.asList(
            AccountNumberPrefixType.FACILITY_NAME, AccountNumberPrefixType.RECEIPT_SHORT_NAME));
    public final static Set<AccountNumberPrefixType> accountNumberPrefixesForFacility = new HashSet<>(Arrays.asList(
    		AccountNumberPrefixType.FACILITY_NAME));
    public final static Set<AccountNumberPrefixType> accountNumberPrefixesForGroups = new HashSet<>(Arrays.asList(
    		AccountNumberPrefixType.FACILITY_NAME));

    public enum AccountNumberPrefixType {
        FACILITY_NAME(1, "accountNumberPrefixType.facilityName"), 
        PATIENT_TYPE(101, "accountNumberPrefixType.patientType"), 
        INVOICE_SHORT_NAME( 201, "accountNumberPrefixType.invoiceShortName"), 
        RECEIPT_SHORT_NAME(301, "accountNumberPrefixType.receiptShortName");

        private final Integer value;
        private final String code;

        private AccountNumberPrefixType(final Integer value, final String code) {
            this.value = value;
            this.code = code;
        }

        public Integer getValue() {
            return this.value;
        }

        public String getCode() {
            return this.code;
        }

        private static final Map<Integer, AccountNumberPrefixType> intToEnumMap = new HashMap<>();
        private static int minValue;
        private static int maxValue;
        static {
            int i = 0;
            for (final AccountNumberPrefixType type : AccountNumberPrefixType.values()) {
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

        public static AccountNumberPrefixType fromInt(final int i) {
            final AccountNumberPrefixType type = intToEnumMap.get(Integer.valueOf(i));
            return type;
        }

        public static int getMinValue() {
            return minValue;
        }

        public static int getMaxValue() {
            return maxValue;
        }

    }

    public static EnumOptionData entityAccountType(final Integer accountTypeId) {
        return AccountNumberFormatEnumerations.entityAccountType(EntityAccountType.fromInt(accountTypeId));
    }

    public static List<EnumOptionData> entityAccountType(final EntityAccountType[] entityAccountTypes) {
        final List<EnumOptionData> optionDatas = new ArrayList<>();
        for (final EntityAccountType accountType : entityAccountTypes) {
            optionDatas.add(entityAccountType(accountType));
        }
        return optionDatas;
    }

    public static EnumOptionData entityAccountType(final EntityAccountType accountType) {
        final EnumOptionData optionData = new EnumOptionData(accountType.getValue().longValue(), accountType.getCode(),
                accountType.toString());
        return optionData;
    }

    public static EnumOptionData accountNumberPrefixType(final Integer accountNumberPrefixTypeId) {
        return AccountNumberFormatEnumerations.entityAccountType(AccountNumberPrefixType.fromInt(accountNumberPrefixTypeId));
    }

    public static List<EnumOptionData> accountNumberPrefixType(final AccountNumberPrefixType[] accountNumberPrefixTypes) {
        final List<EnumOptionData> optionDatas = new ArrayList<>();
        for (final AccountNumberPrefixType accountNumberPrefixType : accountNumberPrefixTypes) {
            optionDatas.add(entityAccountType(accountNumberPrefixType));
        }
        return optionDatas;
    }

    public static EnumOptionData entityAccountType(final AccountNumberPrefixType accountNumberPrefixType) {
        final EnumOptionData optionData = new EnumOptionData(accountNumberPrefixType.getValue().longValue(),
                accountNumberPrefixType.getCode(), accountNumberPrefixType.toString());
        return optionData;
    }

    public static List<EnumOptionData> accountNumberPrefixType(Object[] array) {
        AccountNumberPrefixType[] accountNumberPrefixTypes = Arrays.copyOf(array, array.length, AccountNumberPrefixType[].class);
        return accountNumberPrefixType(accountNumberPrefixTypes);
    }

}

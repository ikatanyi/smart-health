 
package io.smarthealth.infrastructure.accountnumberformat.service;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import io.smarthealth.infrastructure.accountnumberformat.domain.AccountNumberFormatEnumerations.AccountNumberPrefixType;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * /**
 * Example {@link AccountNumberGenerator} for clients that takes an entities
 * auto generated database id and zero fills it ensuring the identifier is
 * always of a given <code>maxLength</code>.
 *
 * @author Kelsas
 */
@Service
public class AccountNumberGenerator {

    private final static int maxLength = 9;

    private final static String ID = "id";
    private final static String PATIENT_TYPE = "patientType";
    private final static String FACILITY_NAME = "facilityName";
    private final static String INVOICE_SHORT_NAME = "invoiceShortName";
    private final static String RECEIPT_SHORT_NAME = "receiptShortName";
    private final static String SHARE_PRODUCT_SHORT_NAME = "sharesProductShortName";

    public String generate(Patient client, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, client.getId().toString());
        propertyMap.put(FACILITY_NAME, client.getCompanyId());
//        CodeValue clientType = client.clientType();
//        if (clientType != null) {
//            propertyMap.put(PATIENT_TYPE, clientType.label());
//        }
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generate(Invoice invoice, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, invoice.getId().toString());
        propertyMap.put(FACILITY_NAME, invoice.getCompanyId());
        propertyMap.put(INVOICE_SHORT_NAME, "Inv");
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generate(Payment payment, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, payment.getId().toString());
        propertyMap.put(FACILITY_NAME, payment.getCompanyId());
        propertyMap.put(RECEIPT_SHORT_NAME, "RCT");
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }
    
     public String generate(Visit visit, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, visit.getId().toString());
        propertyMap.put(FACILITY_NAME, visit.getCompanyId());
        propertyMap.put(RECEIPT_SHORT_NAME, "RCT");
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

//    public String generate(ShareAccount shareaccount, AccountNumberFormat accountNumberFormat) {
//    	Map<String, String> propertyMap = new HashMap<>();
//    	propertyMap.put(ID, shareaccount.getId().toString());
//    	propertyMap.put(SHARE_PRODUCT_SHORT_NAME, shareaccount.getShareProduct().getShortName());
//    	return generateAccountNumber(propertyMap, accountNumberFormat) ;
//    }
    private String generateAccountNumber(Map<String, String> propertyMap, AccountNumberFormat accountNumberFormat) {
        String accountNumber = StringUtils.leftPad(propertyMap.get(ID), AccountNumberGenerator.maxLength, '0');
        if (accountNumberFormat != null && accountNumberFormat.getPrefixEnum() != null) {
            AccountNumberPrefixType accountNumberPrefixType = AccountNumberPrefixType.fromInt(accountNumberFormat.getPrefixEnum());
            String prefix = null;
            switch (accountNumberPrefixType) {
                case PATIENT_TYPE:
                    prefix = propertyMap.get(PATIENT_TYPE);
                    break;

                case FACILITY_NAME:
                    prefix = propertyMap.get(FACILITY_NAME);
                    break;

                case INVOICE_SHORT_NAME:
                    prefix = propertyMap.get(INVOICE_SHORT_NAME);
                    break;

                case RECEIPT_SHORT_NAME:
                    prefix = propertyMap.get(RECEIPT_SHORT_NAME);
                    break;

                default:
                    break;

            }

            // Because account_no is limited to 20 chars, we can only use the first 10 chars of prefix - trim if necessary
            if (prefix != null) {
                prefix = prefix.substring(0, Math.min(prefix.length(), 10));
            }

            accountNumber = StringUtils.overlay(accountNumber, prefix, 0, 0);
        }
        return accountNumber;
    }

    public String generateFacilityAccountNumber(Facility group, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, group.getId().toString());
        propertyMap.put(FACILITY_NAME, group.getFacilityName());
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

}

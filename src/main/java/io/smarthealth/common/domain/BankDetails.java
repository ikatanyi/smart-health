package io.smarthealth.common.domain;

import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class BankDetails {
    //Bank details I need to save
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String bankSwiftCode;
    /** BankDetails Account International BankDetails Account Number */
    private String bankIban;  
}

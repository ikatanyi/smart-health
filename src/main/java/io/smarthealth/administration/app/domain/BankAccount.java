package io.smarthealth.administration.app.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author Kelsas
 */
@Data
@Embeddable
public class BankAccount implements Serializable {
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private String swiftNumber;
}

package io.smarthealth.administration.app.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Embeddable
public class BankEmbedded implements Serializable {
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private String swiftNumber;
}

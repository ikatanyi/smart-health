package io.smarthealth.accounting.acc.events;

import io.smarthealth.infrastructure.utility.UuidGenerator;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class JournalEvent implements Serializable {

    private String _id = UuidGenerator.newUuid();
    private String transactionIdentifier;

    public JournalEvent(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

}

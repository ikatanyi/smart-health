package io.smarthealth.accounting.accounts.events;

//import io.smarthealth.infrastructure.utility.UuidGenerator;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
public class JournalEvent implements Serializable {

    private String _id ;//= UuidGenerator.newUuid();
    private String transactionIdentifier;

    public JournalEvent(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

}

package io.smarthealth.debtor.claim.creditNote.domain;

import io.smarthealth.stock.item.data.SimpleItemData;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class CreditNoteItemData {

    private Long id;
    private Long billItemId;
    private SimpleItemData itemData;
    private Double amount;
}

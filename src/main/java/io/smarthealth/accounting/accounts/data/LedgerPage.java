package io.smarthealth.accounting.accounts.data;

import java.util.List;
import lombok.Data;

@Data
public class LedgerPage {

    private List<LedgerData> ledgers;
    private Integer totalPages;
    private Long totalElements;

    public LedgerPage() {
        super();
    }

}

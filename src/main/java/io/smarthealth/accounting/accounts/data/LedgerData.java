package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.Ledger;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class LedgerData {

    @NotNull
    private AccountType type;
    private String identifier;
    @NotEmpty
    private String name;
    private String description;
    private String parentLedgerIdentifier;
    @Valid
    private List<LedgerData> subLedgers;
    private BigDecimal totalValue;
    private String createdOn;
    private String createdBy;
    @NotNull
    private Boolean showAccountsInChart;

    public LedgerData() {
        super();
    }

    public static LedgerData map(final Ledger ledgerEntity) {
        final LedgerData data = new LedgerData();
        
        data.setType(ledgerEntity.getType());
        
        data.setIdentifier(ledgerEntity.getIdentifier());
        data.setName(ledgerEntity.getName());
        data.setDescription(ledgerEntity.getDescription());
        if (ledgerEntity.getParentLedger() != null) {
            data.setParentLedgerIdentifier(ledgerEntity.getParentLedger().getIdentifier());
        }
        data.setCreatedBy(ledgerEntity.getCreatedBy());
        data.setShowAccountsInChart(ledgerEntity.getShowAccountsInChart());
        final BigDecimal totalValue = ledgerEntity.getTotalValue() != null ? ledgerEntity.getTotalValue() : BigDecimal.ZERO;
        data.setTotalValue(totalValue);
        return data;
    }
}

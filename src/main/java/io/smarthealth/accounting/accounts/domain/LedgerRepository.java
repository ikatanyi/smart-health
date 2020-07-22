package io.smarthealth.accounting.accounts.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long>, JpaSpecificationExecutor<Ledger> {

    List<Ledger> findByParentLedgerIsNull();

    List<Ledger> findByParentLedgerIsNullAndAccountType(final AccountType type);

    List<Ledger> findByParentLedgerOrderByIdentifier(final Ledger parentLedger);

    Optional<Ledger> findByIdentifier(final String identifier);
    
}

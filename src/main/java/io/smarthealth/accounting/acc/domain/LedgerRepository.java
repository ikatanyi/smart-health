package io.smarthealth.accounting.acc.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntity, Long>, JpaSpecificationExecutor<LedgerEntity> {

    List<LedgerEntity> findByParentLedgerIsNull();

    List<LedgerEntity> findByParentLedgerIsNullAndType(final String type);

    List<LedgerEntity> findByParentLedgerOrderByIdentifier(final LedgerEntity parentLedger);

    LedgerEntity findByIdentifier(final String identifier);
}

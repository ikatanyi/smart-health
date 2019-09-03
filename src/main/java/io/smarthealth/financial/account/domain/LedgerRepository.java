/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long>,JpaSpecificationExecutor<Ledger> {

  List<Ledger> findByParentLedgerIsNull();

  List<Ledger> findByParentLedgerIsNullAndType(final String type);

  List<Ledger> findByParentLedgerOrderByIdentifier(final Ledger parentLedger);

  Optional<Ledger> findByIdentifier(final String identifier);
    
}

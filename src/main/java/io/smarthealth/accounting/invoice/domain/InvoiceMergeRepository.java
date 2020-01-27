package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.debtor.claim.processing.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Repository
public interface InvoiceMergeRepository extends JpaRepository<InvoiceMerge, Long>, JpaSpecificationExecutor<InvoiceMerge>{
}

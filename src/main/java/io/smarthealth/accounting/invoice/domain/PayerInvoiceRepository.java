package io.smarthealth.accounting.invoice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PayerInvoiceRepository extends JpaRepository<PayerInvoice, Long> {
    
}

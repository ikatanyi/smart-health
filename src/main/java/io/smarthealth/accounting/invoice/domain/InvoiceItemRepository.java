package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long>, JpaSpecificationExecutor<InvoiceItem> {
 
   Optional<InvoiceItem> findByInvoiceAndBillItem(Invoice invoice, PatientBillItem item);
}

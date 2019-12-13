package io.smarthealth.stock.purchase.domain;

import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long> {

    Optional<PurchaseInvoice> findByInvoiceNumber(String invoiceNumber);
    
    Page<PurchaseInvoice> findByStatus(PurchaseInvoiceStatus status, Pageable page);
}

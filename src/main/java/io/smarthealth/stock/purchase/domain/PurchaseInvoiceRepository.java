package io.smarthealth.stock.purchase.domain;

import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long>, JpaSpecificationExecutor<PurchaseInvoice> {

    Optional<PurchaseInvoice> findByInvoiceNumber(String invoiceNumber);
    
    @Query("SELECT p FROM PurchaseInvoice p WHERE p.invoiceNumber =:inv AND p.supplier.id =:supperId")
    Optional<PurchaseInvoice> findByInvoiceForSupplier(@Param("inv") String invNo, @Param("supperId") Long supperId);
    
    Page<PurchaseInvoice> findByStatus(PurchaseInvoiceStatus status, Pageable page);
}

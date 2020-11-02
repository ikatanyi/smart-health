package io.smarthealth.accounting.invoice.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    public Optional<Invoice> findByNumber(String invoice);

//    public Page<Invoice> findByItemsVoidedFalse(Specification spec, Pageable page);

    @Modifying
    @Query("UPDATE Invoice i SET i.status=:status WHERE i.id=:id")
    int updateInvoiceStatus(@Param("status") InvoiceStatus status, @Param("id") Long id);
}

package io.smarthealth.accounting.invoice.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.clinical.visit.domain.Visit;

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

    List<Invoice> findByVisitAndScheme(Visit visit, Scheme scheme);

}

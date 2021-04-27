package io.smarthealth.supplier.domain;

import java.util.Optional;

import io.smarthealth.supplier.data.SupplierBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier>, StatementRepository {
   Optional<Supplier> findBySupplierNameOrLegalName(String supplierName, String legalName);

   @Query("SELECT new io.smarthealth.supplier.data.SupplierBalance(s.id, s.supplierName, s.supplierType, sum(p.invoiceAmount),c.fullName,c.telephone,c.mobile,c.email,s.active) FROM Supplier s left join PurchaseInvoice p on s.id = p.supplier.id left join Contact c on s.contact.id = c.id group by s.id order by s.supplierName")
   Page<SupplierBalance> findSupplierBalance(Pageable page);
}

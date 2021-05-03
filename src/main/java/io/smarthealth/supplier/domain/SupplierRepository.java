package io.smarthealth.supplier.domain;

import java.util.Optional;

import io.smarthealth.supplier.data.SupplierBalance;
import io.smarthealth.supplier.data.SupplierBalanceAging;
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


   @Query(value = "SELECT supplier_id as supplierId, supplier_name as supplierName, sum(currents) AS currentBalance,  sum(Balance30) as Balance30, sum(Balance60) as balance60, sum(Balance90) as balance90, sum(Balance120) as balance120, SUM(totals) AS total FROM ( select supplier_id, supplier_name,case when datediff (current_date(), invoice_date) <= 1 then invoice_balance else 0 end as currents, case When datediff (current_date(), invoice_date) > 1 and datediff (current_date(), invoice_date) <= 30 then invoice_balance Else 0 End as Balance30, case When datediff (current_date(), invoice_date) > 30 and datediff (current_date(), invoice_date) <= 60 then invoice_balance Else 0 End as Balance60, case When datediff (current_date(), invoice_date) > 60  and datediff (current_date(), invoice_date) <= 90 then invoice_balance Else 0 End as Balance90, case when datediff (current_date(), invoice_date) > 90 then invoice_balance else 0 End as Balance120 , invoice_balance AS totals FROM supplier s LEFT JOIN purchase_invoice i ON s.id=i.supplier_id WHERE i.`type`='Stock_Delivery') as supplier_age_balance group BY supplier_id",  countQuery = "SELECT COUNT(supplier_name) FROM supplier", nativeQuery = true)
   Page<SupplierBalanceAging> findSupplierAgingBalance(Pageable page);

}

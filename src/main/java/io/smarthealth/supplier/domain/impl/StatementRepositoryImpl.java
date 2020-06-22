 package io.smarthealth.supplier.domain.impl;

import io.smarthealth.supplier.data.SupplierStatement;
import io.smarthealth.supplier.domain.StatementRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public class StatementRepositoryImpl implements StatementRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SupplierStatement> getSupplierStatement(Long supplierId) {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT supplierId, supplierName, invoiceDate, description, narration,voucherNo, cheque, invoiceNo, originalAmount, balanceAmount, SUM(balanceAmount) over (PARTITION By supplierId ORDER BY id,supplierId) as runningAmount FROM ( SELECT p.id, p.supplier_id AS supplierId, s.supplier_name AS supplierName, p.invoice_date as invoiceDate, p.type as description, 'Pharmaceuticals' as narration,'' as voucherNo, '' as cheque, invoice_number as invoiceNo, invoice_amount as originalAmount, invoice_balance as balanceAmount FROM purchase_invoice p CROSS JOIN supplier s WHERE p.supplier_id=s.id AND invoice_balance <> 0 UNION ALL SELECT p.id, p.payee_id AS supplierId, s.supplier_name AS supplierName, p.payment_date AS invoiceDate, 'Payments' AS description, '' AS narration, p.voucher_no AS voucherNo, reference_number AS cheque, '' AS invoiceNo, amount AS originalAmount, amount*-1 AS balanceAmount FROM acc_payments p CROSS JOIN supplier s WHERE p.payee_id=s.id AND p.payee_type=1 ORDER BY 3,2 ) AS supplier_statement WHERE supplierId='")
                .append(supplierId).append("'");
         
        Query query = entityManager
                .createNativeQuery(sb.toString())
//                .createNativeQuery("SELECT s.supplier_name AS supplierName, p.invoice_date as invoiceDate, 'Stocks Delivery' as description, 'Pharmaceuticals' as narration,'' as voucherNo, '' as cheque, invoice_number as invoiceNo, invoice_amount as originalAmount, invoice_balance as balanceAmount, SUM(invoice_balance) over (PARTITION By supplier_id ORDER BY p.id,p.supplier_id) as runningAmount FROM purchase_invoice p CROSS JOIN supplier s WHERE p.supplier_id=s.id ")
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(
                        Transformers.aliasToBean(SupplierStatement.class)
                );
        List<SupplierStatement> list = query.getResultList();
        return list;
    }

    @Override
    public BigDecimal getSupplierBalance(Long supplierId, LocalDate date) {
        Query query = entityManager.createNativeQuery("SELECT SUM(invoice_balance)  AS balance FROM purchase_invoice p CROSS JOIN supplier s WHERE p.supplier_id=s.id AND p.supplier_id=" + supplierId, BigDecimal.class);
        List<BigDecimal> results = query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return BigDecimal.ZERO;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}

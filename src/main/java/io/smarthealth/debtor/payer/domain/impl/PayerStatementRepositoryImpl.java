package io.smarthealth.debtor.payer.domain.impl;

import io.smarthealth.debtor.payer.data.PayerStatement;
import io.smarthealth.debtor.payer.domain.PayerStatementRepository;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.supplier.data.SupplierStatement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public class PayerStatementRepositoryImpl implements PayerStatementRepository {

    @Autowired
    private EntityManager em;

    @Override
    public List<PayerStatement> getPayerStatement(Long payerId, DateRange range) {
        Query query = em
                .createNativeQuery(getSchema(payerId, range))
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(
                        Transformers.aliasToBean(PayerStatement.class)
                );
        List<PayerStatement> list = query.getResultList();
        return list;
    }

    @Override
    public BigDecimal getPayerBalance(Long payerId, LocalDate date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String getSchema(Long payerId, DateRange range) {
        StringBuilder sb = new StringBuilder("SELECT payerId, payerName, date, transactionType, description, reference, invoiceNo, originalAmount, balanceAmount,  SUM(originalAmount) over (PARTITION By payerId ORDER BY DATE, payerId) as runningAmount  FROM ( SELECT p.id AS payerId, p.payer_code as payerCode, p.payer_name as payerName, i.invoice_date AS date, i.due_date as dueDate, 'Invoicing' AS transactionType, i.member_name AS description, i.member_number AS reference,i.number as invoiceNo, i.amount AS originalAmount, i.balance AS balanceAmount FROM patient_invoice i CROSS JOIN payers p WHERE p.id=i.payer_id UNION SELECT p.id AS payerId, p.payer_code as payerCode, p.payer_name as payerName,r.remittance_date AS DATE, r.remittance_date AS dueDate, 'Payments' AS transactionType, r.remittance_no AS description, rc.reference_number AS reference ,rc.receipt_no AS invoiceNo, rc.amount *-1  AS originalAmount, rc.amount*-1 AS balanceAmount FROM acc_receipts rc CROSS JOIN acc_remittance r CROSS JOIN payers p WHERE rc.id=r.receipt_id AND  p.id=r.payer_id UNION SELECT p.id AS payerId, p.payer_code as payerCode, p.payer_name as payerName,c.credit_date AS DATE, c.credit_date AS dueDate, 'Credit Note' AS transactionType, c.comments AS description, c.credit_note_no AS reference ,c.credit_note_no AS invoiceNo, c.amount *-1  AS originalAmount, c.amount*-1 AS balanceAmount FROM patient_credit_note c CROSS JOIN payers p WHERE p.id=c.payer_id ORDER BY 4,1 ) AS debtor_statement ");
        if (payerId != null || range != null) {
            sb.append(" where ");
            if (payerId != null) {
                sb.append(" payerId =").append(payerId).append(" ");
            }
            if (payerId != null && range != null) {
                sb.append(" and ");
            }
            if (range != null) {
                sb.append(" date between ").append(range.getStartDateTime()).append(" and ").append(range.getEndDateTime()).append(" ");
            }
        }
        return sb.toString();
    }
}

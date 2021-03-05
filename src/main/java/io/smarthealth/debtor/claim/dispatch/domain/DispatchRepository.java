/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.claim.dispatch.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface DispatchRepository extends JpaRepository<Dispatch, Long>, JpaSpecificationExecutor<Dispatch>{
    Optional<Dispatch>findByDispatchNo(String dispatchNo);
    
    @Query(value="SELECT 'Under 30 Days' AS description, SUM(i.amount) AS amount, p.payer_name AS payerName, d.dispatch_date AS ddate FROM patient_invoice i " +
    " LEFT JOIN patient_invoice_dispatch d ON i.dispatched_invoice_id=d.id" +
    " LEFT JOIN payers p ON i.payer_id=p.id" +
    " WHERE STATUS='sent' AND (ISNULL(:payerId) OR i.payer_id=:payerId)" +
    " GROUP BY  d.payer_id HAVING DATEDIFF(CURDATE(),d.dispatch_date)<=30" +
    " UNION" +
    " SELECT 'Between 31 AND 90 Days' AS description, SUM(i.amount) AS amount, p.payer_name AS payerName, d.dispatch_date AS ddate FROM patient_invoice i " +
    " LEFT JOIN patient_invoice_dispatch d ON i.dispatched_invoice_id=d.id" +
    " LEFT JOIN payers p ON i.payer_id=p.id" +
    " WHERE STATUS='sent'  AND (ISNULL(:payerId) OR i.payer_id=:payerId)" +
    " GROUP BY  d.payer_id HAVING DATEDIFF(CURDATE(),d.dispatch_date) BETWEEN 30 AND 90" +
    " UNION" +
    " SELECT 'Over 90 Days' AS description, SUM(i.amount) AS amount, p.payer_name AS payerName, d.dispatch_date AS ddate FROM patient_invoice i " +
    " LEFT JOIN patient_invoice_dispatch d ON i.dispatched_invoice_id=d.id" +
    " LEFT JOIN payers p ON i.payer_id=p.id" +
    " WHERE STATUS='sent'  AND (ISNULL(:payerId) OR i.payer_id=:payerId)" +
    " GROUP BY d.payer_id HAVING DATEDIFF(CURDATE(),d.dispatch_date)>90", nativeQuery = true)
    List<InvoiceAgeSummaryInterface>getInvoiceAgeSummary(Long payerId);
}

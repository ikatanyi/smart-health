package io.smarthealth.accounting.payment.domain.repository;

import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.ReceiptTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface ReceiptTransactionRepository extends JpaRepository<ReceiptTransaction, Long>, JpaSpecificationExecutor<ReceiptTransaction> {

    List<ReceiptTransaction> findByReceipt(Receipt receipt);

    @Modifying
    @Query("DELETE FROM ReceiptTransaction t WHERE  t.receipt.receiptNo = :receiptNo")
    void deleteReceiptTransaction(@Param("receiptNo") String receiptNo);

}

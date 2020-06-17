package io.smarthealth.accounting.payment.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long>, JpaSpecificationExecutor<ReceiptItem> {

}

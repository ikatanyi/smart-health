package io.smarthealth.accounting.payment.domain.repository;

import io.smarthealth.accounting.payment.domain.PaymentDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface ReceivePaymenttRepository extends JpaRepository<PaymentDeposit, Long>, JpaSpecificationExecutor<PaymentDeposit> {

}

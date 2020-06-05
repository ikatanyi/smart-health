package io.smarthealth.accounting.payment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface PettyCashPaymentRepository extends JpaRepository<PettyCashPayment, Long> {
  List<PettyCashPayment>findByPayment(Payment payment);
}

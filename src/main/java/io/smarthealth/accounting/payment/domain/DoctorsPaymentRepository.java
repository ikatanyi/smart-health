package io.smarthealth.accounting.payment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface DoctorsPaymentRepository extends JpaRepository<DoctorsPayment, Long>, JpaSpecificationExecutor<DoctorsPayment> {
     List<DoctorsPayment>findByPayment(Payment payment);
}

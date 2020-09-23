package io.smarthealth.accounting.payment.domain.repository;

import io.smarthealth.accounting.payment.domain.Prepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface PrepaymentRepository extends JpaRepository<Prepayment, Long>, JpaSpecificationExecutor<Prepayment> {

}

package io.smarthealth.accounting.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface RemittanceRepository extends JpaRepository<Remittance, Long> {

}

package io.smarthealth.accounting.payment.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface CopaymentRepository extends JpaRepository<Copayment, Long>, JpaSpecificationExecutor<Copayment> {

    public Optional<Copayment> findByVisitAndAmountEqualsAndPaidFalse(Visit visit, BigDecimal amount);
}

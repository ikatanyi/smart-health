package io.smarthealth.accounting.payment.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface RemittanceRepository extends JpaRepository<Remittance, Long>, JpaSpecificationExecutor<Remittance> {

    @Modifying
    @Query("update Remittance R set R.balance=:balance where R.id=:id")
    int updateBalance(@Param("balance") BigDecimal balance, @Param("id") Long id);

    Optional<Remittance> findByReceipt(final Receipt receipt);

}

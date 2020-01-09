package io.smarthealth.accounting.smart.domain;

import io.smarthealth.supplier.domain.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ExchangeFileRepository extends JpaRepository<ExchangeFile, Long> {
   Optional<ExchangeFile> findByMemberNrAndProgressFlag(String MemberNr, Long ProgressFlag);
}

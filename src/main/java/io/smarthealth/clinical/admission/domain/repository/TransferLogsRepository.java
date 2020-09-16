package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.TransferLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface TransferLogsRepository extends JpaRepository<TransferLogs, Long>,JpaSpecificationExecutor<TransferLogs> {
   
}

package io.smarthealth.infrastructure.jobs.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface JobParameterRepository extends JpaRepository<JobParameter, Long>, JpaSpecificationExecutor<JobParameter> {

    List<JobParameter> findByJobId(Long jobId);
}

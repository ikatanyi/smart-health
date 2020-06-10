package io.smarthealth.infrastructure.jobs.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface SchedulerDetailRepository extends JpaRepository<SchedulerDetail, Long>, JpaSpecificationExecutor<SchedulerDetail> {

}

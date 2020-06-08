package io.smarthealth.infrastructure.jobs.domain;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface ScheduledJobDetailRepository extends JpaRepository<ScheduledJobDetail, Long>, JpaSpecificationExecutor<ScheduledJobDetail> {

    ScheduledJobDetail findByJobKey(String jobKey);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select jobDetail from ScheduledJobDetail jobDetail where jobDetail.jobKey = :jobKey")
    ScheduledJobDetail findByJobKeyWithLock(@Param("jobKey") String jobKey);

}

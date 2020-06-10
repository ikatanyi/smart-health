/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.scheduledjobs.service;

import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import io.smarthealth.infrastructure.jobs.service.JobName;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service(value = "scheduledJobRunnerService")
public class ScheduledJobRunnerServiceImpl implements ScheduledJobRunnerService {

    @Transactional
    @Override
//    @CronTarget(jobName = JobName.AUTO_CHECK_OUT_PATIENT)
    public void updateCheckout() {
        log.info("Patient checkout scheduling effected .... " + LocalDateTime.now().toString());
    }

}

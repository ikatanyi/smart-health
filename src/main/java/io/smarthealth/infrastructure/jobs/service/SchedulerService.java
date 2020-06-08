/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.service;

import io.smarthealth.infrastructure.jobs.data.JobDetailData;
import io.smarthealth.infrastructure.jobs.data.JobDetailHistoryData;
import io.smarthealth.infrastructure.jobs.data.UpdateJobDetailData;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobDetail;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobRunHistory;
import io.smarthealth.infrastructure.jobs.domain.SchedulerDetail;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author Kelsas
 */
public interface SchedulerService {

    public List<ScheduledJobDetail> retrieveAllJobs();

    public ScheduledJobDetail findByJobKey(String triggerKey);

    public void saveOrUpdate(ScheduledJobDetail scheduledJobDetails);

    public void saveOrUpdate(ScheduledJobDetail scheduledJobDetails, ScheduledJobRunHistory scheduledJobRunHistory);

    public Long fetchMaxVersionBy(String triggerKey);

    public ScheduledJobDetail findByJobId(Long jobId);

    public JobDetailData updateJobDetail(Long jobId, UpdateJobDetailData command);

    public SchedulerDetail retriveSchedulerDetail();

    public void updateSchedulerDetail(final SchedulerDetail schedulerDetail);

    public boolean processJobDetailForExecution(String jobKey, String triggerType);

    public List<JobDetailData> findAllJobDeatils();

    public JobDetailData retrieveOne(Long jobId);

    public List<JobDetailHistoryData> retrieveJobHistory(Long jobId, Pageable page);

    public boolean isUpdatesAllowed();
}

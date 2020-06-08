package io.smarthealth.infrastructure.jobs.service.impl;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.jobs.data.JobDetailData;
import io.smarthealth.infrastructure.jobs.data.JobDetailHistoryData;
import io.smarthealth.infrastructure.jobs.data.UpdateJobDetailData;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobDetail;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobDetailRepository;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobRunHistory;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobRunHistoryRepository;
import io.smarthealth.infrastructure.jobs.domain.SchedulerDetail;
import io.smarthealth.infrastructure.jobs.domain.SchedulerDetailRepository;
import io.smarthealth.infrastructure.jobs.exception.OperationNotAllowedException;
import io.smarthealth.infrastructure.jobs.service.SchedulerService;
import io.smarthealth.infrastructure.utility.DateUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final ScheduledJobDetailRepository scheduledJobDetailsRepository;
    private final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository;
    private final SchedulerDetailRepository schedulerDetailRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ScheduledJobDetail> retrieveAllJobs() {
        return this.scheduledJobDetailsRepository.findAll();
    }

    @Override
    public ScheduledJobDetail findByJobKey(final String jobKey) {
        return this.scheduledJobDetailsRepository.findByJobKey(jobKey);
    }

    @Override
    public void saveOrUpdate(ScheduledJobDetail scheduledJobDetails) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
    }

    @Override
    public void saveOrUpdate(ScheduledJobDetail scheduledJobDetails, ScheduledJobRunHistory scheduledJobRunHistory) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
        this.scheduledJobRunHistoryRepository.save(scheduledJobRunHistory);
    }

    @Override
    public Long fetchMaxVersionBy(String jobKey) {
        Long version = 0L;
        final Long versionFromDB = this.scheduledJobRunHistoryRepository.findMaxVersionByJobKey(jobKey);
        if (versionFromDB != null) {
            version = versionFromDB;
        }
        return version;
    }

    @Override
    public ScheduledJobDetail findByJobId(Long jobId) {
        return this.scheduledJobDetailsRepository.findById(jobId).orElse(null);
    }

    @Override
    public JobDetailData updateJobDetail(Long jobId, UpdateJobDetailData data) {
        ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);

        if (scheduledJobDetail == null) {
            throw APIException.notFound(String.valueOf(jobId));
        }
        scheduledJobDetail.setJobDisplayName(data.getDisplayName());
        scheduledJobDetail.setCronExpression(data.getCronExpression());
        scheduledJobDetail.setActiveSchedular(data.isActive());

        scheduledJobDetailsRepository.saveAndFlush(scheduledJobDetail);

        JobDetailData jobDetailData = new JobDetailData(jobId,
                scheduledJobDetail.getJobDisplayName(),
                scheduledJobDetail.getNextRunTime(),
                scheduledJobDetail.getErrorLog(),
                scheduledJobDetail.getCronExpression(),
                scheduledJobDetail.isActiveSchedular(), scheduledJobDetail.isCurrentlyRunning(), null);
        return jobDetailData;
    }

    @Override
    public SchedulerDetail retriveSchedulerDetail() {
        SchedulerDetail schedulerDetail = null;
        final List<SchedulerDetail> schedulerDetailList = this.schedulerDetailRepository.findAll();
        if (schedulerDetailList != null && !schedulerDetailList.isEmpty()) {
            schedulerDetail = schedulerDetailList.get(0);
        }
        return schedulerDetail;
    }

    @Transactional
    @Override
    public void updateSchedulerDetail(SchedulerDetail schedulerDetail) {
        this.schedulerDetailRepository.save(schedulerDetail);
    }

    @Transactional
    @Override
    public boolean processJobDetailForExecution(String jobKey, String triggerType) {
        boolean isStopExecution = false;
        final ScheduledJobDetail scheduledJobDetail = this.scheduledJobDetailsRepository.findByJobKeyWithLock(jobKey);

        if (scheduledJobDetail != null) {

            if (scheduledJobDetail.isCurrentlyRunning()
                    || triggerType.equals("cron") && scheduledJobDetail.getNextRunTime().isAfter(LocalDateTime.now())) {
                isStopExecution = true;
            }
            
            final SchedulerDetail schedulerDetail = retriveSchedulerDetail();

            if (schedulerDetail != null) {
                if (triggerType.equals("cron") && schedulerDetail.isSuspended()) {
                    scheduledJobDetail.setTriggerMisfired(true);
                    isStopExecution = true;
                } else if (!isStopExecution) {
                    scheduledJobDetail.setCurrentlyRunning(true);
                }
                this.scheduledJobDetailsRepository.save(scheduledJobDetail);
            }
        }
        
        return isStopExecution;
    }

    @Override
    public List<JobDetailData> findAllJobDeatils() {
        final JobDetailMapper detailMapper = new JobDetailMapper();
        final String sql = detailMapper.schema();
        final List<JobDetailData> JobDeatils = this.jdbcTemplate.query(sql, detailMapper, new Object[]{});
        return JobDeatils;
    }

    @Override
    public JobDetailData retrieveOne(Long jobId) {
        try {
            final JobDetailMapper detailMapper = new JobDetailMapper();
            final String sql = detailMapper.schema() + " where job.id=?";
            return this.jdbcTemplate.queryForObject(sql, detailMapper, new Object[]{jobId});
        } catch (final EmptyResultDataAccessException e) {
            throw APIException.notFound(String.valueOf(jobId));
        }
    }

    @Override
    public List<JobDetailHistoryData> retrieveJobHistory(Long jobId, Pageable page) {

        if (!isJobExist(jobId)) {
            throw APIException.notFound(String.valueOf(jobId));
        }
        final JobHistoryMapper jobHistoryMapper = new JobHistoryMapper();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append(jobHistoryMapper.schema());
        sqlBuilder.append(" where job.id=?");

//        if (page!=null) {
//            sqlBuilder.append(" limit ").append(page.getPageSize());
//            if (page.getOffset()>0) {
//                sqlBuilder.append(" offset ").append(page.getOffset());
//            }
//        }
        List<JobDetailHistoryData> lists = this.jdbcTemplate.query(sqlBuilder.toString(), jobHistoryMapper, new Object[]{jobId});
//       Pager<JobDetailHistoryData> data = PaginationUtil.paginateList(lists, "Job History Data", "", page);
        return lists;
    }

    @Override
    public boolean isUpdatesAllowed() {
        final String sql = "select job.display_name from job job where job.currently_running=true and job.updates_allowed=false";
        final List<String> names = this.jdbcTemplate.queryForList(sql, String.class);
        if (names != null && names.size() > 0) {
            final String listVals = names.toString();
            final String jobNames = listVals.substring(listVals.indexOf("[") + 1, listVals.indexOf("]"));
            throw new OperationNotAllowedException(jobNames);
        }
        return true;
    }

    private boolean isJobExist(final Long jobId) {
        boolean isJobPresent = false;
        try {
            final String sql = "select count(*) from job job where job.id= ?";
            final int count = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{jobId});
            if (count == 1) {
                isJobPresent = true;
            }
            return isJobPresent;
        } catch (EmptyResultDataAccessException e) {
            return isJobPresent;
        }

    }

    private static final class JobDetailMapper implements RowMapper<JobDetailData> {

        private final StringBuilder sqlBuilder = new StringBuilder("select")
                .append(" job.id,job.display_name as displayName,job.next_run_time as nextRunTime,job.initializing_errorlog as initializingError,job.cron_expression as cronExpression,job.is_active as active,job.currently_running as currentlyRunning,")
                .append(" runHistory.version,runHistory.start_time as lastRunStartTime,runHistory.end_time as lastRunEndTime,runHistory.`status`,runHistory.error_message as jobRunErrorMessage,runHistory.trigger_type as triggerType,runHistory.error_log as jobRunErrorLog ")
                .append(" from job job  left join job_run_history runHistory ON job.id=runHistory.job_id and job.previous_run_start_time=runHistory.start_time ");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public JobDetailData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String displayName = rs.getString("displayName"); 
            final Date nextRunTime = rs.getTimestamp("nextRunTime");
            final String initializingError = rs.getString("initializingError");
            final String cronExpression = rs.getString("cronExpression");
            final boolean active = rs.getBoolean("active");
            final boolean currentlyRunning = rs.getBoolean("currentlyRunning");

            final Long version = rs.getLong("version");
            final Date jobRunStartTime = rs.getTimestamp("lastRunStartTime");
            final Date jobRunEndTime = rs.getTimestamp("lastRunEndTime");
            final String status = rs.getString("status");
            final String jobRunErrorMessage = rs.getString("jobRunErrorMessage");
            final String triggerType = rs.getString("triggerType");
            final String jobRunErrorLog = rs.getString("jobRunErrorLog");

            JobDetailHistoryData lastRunHistory = null;
            if (version > 0) {
                lastRunHistory = new JobDetailHistoryData(version, DateUtility.toLocalDateTime(jobRunStartTime), DateUtility.toLocalDateTime(jobRunEndTime), status, jobRunErrorMessage, triggerType, jobRunErrorLog);
            }

            final JobDetailData jobDetail = new JobDetailData(id, displayName, DateUtility.toLocalDateTime(nextRunTime), initializingError, cronExpression, active, currentlyRunning, lastRunHistory);
            return jobDetail;
        }

    }

    private static final class JobHistoryMapper implements RowMapper<JobDetailHistoryData> {

        private final StringBuilder sqlBuilder = new StringBuilder("Select")
                .append(" runHistory.version,runHistory.start_time as runStartTime,runHistory.end_time as runEndTime,runHistory.`status`,runHistory.error_message as jobRunErrorMessage,runHistory.trigger_type as triggerType,runHistory.error_log as jobRunErrorLog ")
                .append(" from job job join job_run_history runHistory ON job.id=runHistory.job_id");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public JobDetailHistoryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long version = rs.getLong("version");
            final Date jobRunStartTime = rs.getTimestamp("runStartTime");
            final Date jobRunEndTime = rs.getTimestamp("runEndTime");
            final String status = rs.getString("status");
            final String jobRunErrorMessage = rs.getString("jobRunErrorMessage");
            final String triggerType = rs.getString("triggerType");
            final String jobRunErrorLog = rs.getString("jobRunErrorLog");
            final JobDetailHistoryData jobDetailHistory = new JobDetailHistoryData(version, DateUtility.toLocalDateTime(jobRunStartTime), DateUtility.toLocalDateTime(jobRunEndTime), status,
                    jobRunErrorMessage, triggerType, jobRunErrorLog);
            return jobDetailHistory;
        }

    }

}

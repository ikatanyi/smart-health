package io.smarthealth.infrastructure.jobs.service.impl;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.jobs.annotation.CronMethodParser;
import io.smarthealth.infrastructure.jobs.annotation.CronMethodParser.ClassMethodNamesPair;
import io.smarthealth.infrastructure.jobs.domain.JobParameter;
import io.smarthealth.infrastructure.jobs.domain.JobParameterRepository;
import io.smarthealth.infrastructure.jobs.domain.ScheduledJobDetail;
import io.smarthealth.infrastructure.jobs.domain.SchedulerDetail;
import io.smarthealth.infrastructure.jobs.service.JobRegisterService;
import io.smarthealth.infrastructure.jobs.service.SchedulerJobListener;
import io.smarthealth.infrastructure.jobs.service.SchedulerService;
import io.smarthealth.infrastructure.jobs.service.SchedulerServiceConstants;
import io.smarthealth.infrastructure.jobs.service.SchedulerStopListener;
import io.smarthealth.infrastructure.jobs.service.SchedulerTriggerListener;
import io.smarthealth.infrastructure.utility.DateUtility;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.*;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class JobRegisterServiceImpl implements JobRegisterService, ApplicationListener<ContextClosedEvent> {

    private SchedulerService schedulerService;
    private ApplicationContext applicationContext;
    private SchedulerJobListener schedulerJobListener;
    private SchedulerStopListener schedulerStopListener;
    private SchedulerTriggerListener globalSchedulerTriggerListener;
    private JobParameterRepository jobParameterRepository;

    private final HashMap<String, Scheduler> schedulers = new HashMap<>(4);

    @Autowired
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setSchedulerJobListener(SchedulerJobListener schedulerJobListener) {
        this.schedulerJobListener = schedulerJobListener;
    }

    @Autowired
    public void setSchedulerStopListener(SchedulerStopListener schedulerStopListener) {
        this.schedulerStopListener = schedulerStopListener;
    }

    @Autowired
    public void setGlobalSchedulerTriggerListener(SchedulerTriggerListener globalSchedulerTriggerListener) {
        this.globalSchedulerTriggerListener = globalSchedulerTriggerListener;
    }

    @Autowired
    public void setJobParameterRepository(JobParameterRepository jobParameterRepository) {
        this.jobParameterRepository = jobParameterRepository;
    }

    @PostConstruct
    public void loadAllJobs() {
        final List<ScheduledJobDetail> scheduledJobDetails = this.schedulerService.retrieveAllJobs();
        for (final ScheduledJobDetail jobDetails : scheduledJobDetails) {
            scheduleJob(jobDetails);
            jobDetails.setTriggerMisfired(false);
            this.schedulerService.saveOrUpdate(jobDetails);
        }
        final SchedulerDetail schedulerDetail = this.schedulerService.retriveSchedulerDetail();
        if (schedulerDetail!=null && schedulerDetail.isResetSchedulerOnBootup()) {
            schedulerDetail.updateSuspendedState(false);
            this.schedulerService.updateSchedulerDetail(schedulerDetail);
        }
    }

    public void executeJob(final ScheduledJobDetail scheduledJobDetail, String triggerType) {
        try {
            final JobDataMap jobDataMap = new JobDataMap();
            if (triggerType == null) {
                triggerType = SchedulerServiceConstants.TRIGGER_TYPE_APPLICATION;
            }
            jobDataMap.put(SchedulerServiceConstants.TRIGGER_TYPE_REFERENCE, triggerType);
            final String key = scheduledJobDetail.getJobKey();
            final JobKey jobKey = constructJobKey(key);
            final String schedulerName = getSchedulerName(scheduledJobDetail);
            final Scheduler scheduler = this.schedulers.get(schedulerName);
            if (scheduler == null || !scheduler.checkExists(jobKey)) {
                final JobDetail jobDetail = createJobDetail(scheduledJobDetail);
                final String tempSchedulerName = "temp" + scheduledJobDetail.getId();
                final Scheduler tempScheduler = createScheduler(tempSchedulerName, 1, schedulerJobListener, schedulerStopListener);
                tempScheduler.addJob(jobDetail, true);
                jobDataMap.put(SchedulerServiceConstants.SCHEDULER_NAME, tempSchedulerName);
                this.schedulers.put(tempSchedulerName, tempScheduler);
                tempScheduler.triggerJob(jobDetail.getKey(), jobDataMap);
            } else {
                scheduler.triggerJob(jobKey, jobDataMap);
            }

        } catch (final Exception e) {
            final String msg = "Job execution failed for job with id:" + scheduledJobDetail.getId();
            log.error("{}", msg, e);
            throw APIException.internalError(msg);
        }

    }

    public void rescheduleJob(final ScheduledJobDetail scheduledJobDetail) {
        try {
            final String jobIdentity = scheduledJobDetail.getJobKey();
            final JobKey jobKey = constructJobKey(jobIdentity);
            final String schedulername = getSchedulerName(scheduledJobDetail);
            final Scheduler scheduler = this.schedulers.get(schedulername);
            if (scheduler != null) {
                scheduler.deleteJob(jobKey);
            }
            scheduleJob(scheduledJobDetail);
            this.schedulerService.saveOrUpdate(scheduledJobDetail);
        } catch (final Throwable throwable) {
            final String stackTrace = getStackTraceAsString(throwable);
            scheduledJobDetail.setErrorLog(stackTrace);
            this.schedulerService.saveOrUpdate(scheduledJobDetail);
        }
    }

    @Override
    public void pauseScheduler() {
        final SchedulerDetail schedulerDetail = this.schedulerService.retriveSchedulerDetail();
        if (!schedulerDetail.isSuspended()) {
            schedulerDetail.updateSuspendedState(true);
            this.schedulerService.updateSchedulerDetail(schedulerDetail);
        }
    }

    @Override
    public void startScheduler() {
        final SchedulerDetail schedulerDetail = this.schedulerService.retriveSchedulerDetail();
        if (schedulerDetail!=null && schedulerDetail.isSuspended()) {
            schedulerDetail.updateSuspendedState(false);
            this.schedulerService.updateSchedulerDetail(schedulerDetail);
            if (schedulerDetail.isExecuteInstructionForMisfiredJobs()) {
                final List<ScheduledJobDetail> scheduledJobDetails = this.schedulerService.retrieveAllJobs();
                for (final ScheduledJobDetail jobDetail : scheduledJobDetails) {
                    if (jobDetail.isTriggerMisfired()) {
                        if (jobDetail.isActiveSchedular()) {
                            executeJob(jobDetail, SchedulerServiceConstants.TRIGGER_TYPE_CRON);
                        }
                        final String schedulerName = getSchedulerName(jobDetail);
                        final Scheduler scheduler = this.schedulers.get(schedulerName);
                        if (scheduler != null) {
                            final String key = jobDetail.getJobKey();
                            final JobKey jobKey = constructJobKey(key);
                            try {
                                final List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                                for (final Trigger trigger : triggers) {
                                    if (trigger.getNextFireTime() != null && trigger.getNextFireTime().after(DateUtility.toDateTime(jobDetail.getNextRunTime()))) {
                                        jobDetail.setNextRunTime(DateUtility.toLocalDateTime(trigger.getNextFireTime()));
                                    }
                                }
                            } catch (final SchedulerException e) {
                                log.error("Error occured.", e);
                            }
                        }
                        jobDetail.setTriggerMisfired(false);
                        this.schedulerService.saveOrUpdate(jobDetail);
                    }
                }
            }
        }
    }

    @Override
    public void rescheduleJob(final Long jobId) {
        final ScheduledJobDetail scheduledJobDetail = this.schedulerService.findByJobId(jobId);
        rescheduleJob(scheduledJobDetail);
    }

    @Override
    public void executeJob(final Long jobId) {
        final ScheduledJobDetail scheduledJobDetail = this.schedulerService.findByJobId(jobId);
        if (scheduledJobDetail == null) {
            throw APIException.notFound(String.valueOf(jobId));
        }
        executeJob(scheduledJobDetail, null);
    }

    @Override
    public boolean isSchedulerRunning() {
        return this.schedulerService.retriveSchedulerDetail()!=null ? !this.schedulerService.retriveSchedulerDetail().isSuspended() : false;
    }

    /**
     * Need to use ContextClosedEvent instead of ContextStoppedEvent because in
     * case Spring Boot fails to start-up (e.g.because Tomcat port is already in
     * use) then org.springframework.boot.SpringApplication.run(String...) does
     * a context.close(); and not a context.stop();
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(@SuppressWarnings("unused") ContextClosedEvent event) {
        this.stopAllSchedulers();
    }

    private void scheduleJob(final ScheduledJobDetail scheduledJobDetails) {
        if (!scheduledJobDetails.isActiveSchedular()) {
            scheduledJobDetails.setNextRunTime(null);
            scheduledJobDetails.setCurrentlyRunning(false);
            return;
        }
        try {
            final JobDetail jobDetail = createJobDetail(scheduledJobDetails);
            final Trigger trigger = createTrigger(scheduledJobDetails, jobDetail);
            final Scheduler scheduler = getScheduler(scheduledJobDetails);
            scheduler.scheduleJob(jobDetail, trigger);
            scheduledJobDetails.setJobKey(getJobKeyAsString(jobDetail.getKey()));
            
            
            scheduledJobDetails.setNextRunTime(DateUtility.toLocalDateTime(trigger.getNextFireTime()));
            scheduledJobDetails.setErrorLog(null);
        } catch (final Throwable throwable) {
            scheduledJobDetails.setNextRunTime(null);
            final String stackTrace = getStackTraceAsString(throwable);
            scheduledJobDetails.setErrorLog(stackTrace);
            log.error("Could not schedule job: {}", scheduledJobDetails.getJobName(), throwable);
        }
        scheduledJobDetails.setCurrentlyRunning(false);
    }

    @Override
    public void stopAllSchedulers() {
        for (Scheduler scheduler : this.schedulers.values()) {
            try {
                scheduler.shutdown();
            } catch (final SchedulerException e) {
                log.error("Error occured.", e);
            }
        }
    }

    private Scheduler getScheduler(final ScheduledJobDetail scheduledJobDetail) throws Exception {
        final String schedulername = getSchedulerName(scheduledJobDetail);
        Scheduler scheduler = this.schedulers.get(schedulername);
        if (scheduler == null) {
            int noOfThreads = SchedulerServiceConstants.DEFAULT_THREAD_COUNT;
            if (scheduledJobDetail.getSchedulerGroup() > 0) {
                noOfThreads = SchedulerServiceConstants.GROUP_THREAD_COUNT;
            }
            scheduler = createScheduler(schedulername, noOfThreads, schedulerJobListener);
            this.schedulers.put(schedulername, scheduler);
        }
        return scheduler;
    }

    @Override
    public void stopScheduler(final String name) {
        final Scheduler scheduler = this.schedulers.remove(name);
        try {
            scheduler.shutdown();
        } catch (final SchedulerException e) {
            log.error("Error occured.", e);
        }
    }

    private String getSchedulerName(final ScheduledJobDetail scheduledJobDetail) {
        final StringBuilder sb = new StringBuilder(20);
        if (scheduledJobDetail.getSchedulerGroup() > 0) {
            sb.append(SchedulerServiceConstants.SCHEDULER_GROUP).append(scheduledJobDetail.getSchedulerGroup());
        }
        return sb.toString();
    }

    private Scheduler createScheduler(final String name, final int noOfThreads, JobListener... jobListeners) throws Exception {
        final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerName(name);
        schedulerFactoryBean.setGlobalJobListeners(jobListeners);
        final TriggerListener[] globalTriggerListeners = {globalSchedulerTriggerListener};
        schedulerFactoryBean.setGlobalTriggerListeners(globalTriggerListeners);
        final Properties quartzProperties = new Properties();
        quartzProperties.put(SchedulerFactoryBean.PROP_THREAD_COUNT, Integer.toString(noOfThreads));
        schedulerFactoryBean.setQuartzProperties(quartzProperties);
        schedulerFactoryBean.afterPropertiesSet();
        schedulerFactoryBean.start();
        return schedulerFactoryBean.getScheduler();
    }

    private JobDetail createJobDetail(final ScheduledJobDetail scheduledJobDetail) throws Exception {
        final ClassMethodNamesPair jobDetails = CronMethodParser.findTargetMethodDetails(scheduledJobDetail.getJobName());
        if (jobDetails == null) {
            throw new IllegalArgumentException(
                    "Code has no @CronTarget with this job name (@see JobName); seems like DB/code are not in line: "
                    + scheduledJobDetail.getJobName());
        }
        final Object targetObject = getBeanObject(Class.forName(jobDetails.className));
        final MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactoryBean.setName(scheduledJobDetail.getJobName() + "JobDetail");
        jobDetailFactoryBean.setTargetObject(targetObject);
        jobDetailFactoryBean.setTargetMethod(jobDetails.methodName);
        jobDetailFactoryBean.setGroup(scheduledJobDetail.getGroupName());
        jobDetailFactoryBean.setConcurrent(false);
        jobDetailFactoryBean.afterPropertiesSet();
        return jobDetailFactoryBean.getObject();
    }

    public Map<String, String> getJobParameter(ScheduledJobDetail scheduledJobDetail) {
        List<JobParameter> jobParameterList = jobParameterRepository.findByJobId(scheduledJobDetail.getId());
        Map<String, String> jobParameterMap = new HashMap<>();
        for (JobParameter jobparameter : jobParameterList) {
            jobParameterMap.put(jobparameter.getParameterName(), jobparameter.getParameterValue());
        }
        return jobParameterMap;
    }

    private Object getBeanObject(final Class<?> classType) throws ClassNotFoundException {
        final List<Class<?>> typesList = new ArrayList<>();
        final Class<?>[] interfaceType = classType.getInterfaces();
        if (interfaceType.length > 0) {
            typesList.addAll(Arrays.asList(interfaceType));
        } else {
            Class<?> superclassType = classType;
            while (!Object.class.getName().equals(superclassType.getSuperclass().getName())) {
                superclassType = superclassType.getSuperclass();
            }
            typesList.add(superclassType);
        }
        final List<String> beanNames = new ArrayList<>();
        for (final Class<?> clazz : typesList) {
            beanNames.addAll(Arrays.asList(this.applicationContext.getBeanNamesForType(clazz)));
        }
        Object targetObject = null;
        for (final String beanName : beanNames) {
            final Object nextObject = this.applicationContext.getBean(beanName);
            String targetObjName = nextObject.toString();
            targetObjName = targetObjName.substring(0, targetObjName.lastIndexOf("@"));
            if (classType.getName().equals(targetObjName)) {
                targetObject = nextObject;
                break;
            }
        }
        return targetObject;
    }

    private Trigger createTrigger(final ScheduledJobDetail scheduledJobDetails, final JobDetail jobDetail) throws ParseException {
        final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setName(scheduledJobDetails.getJobName() + "Trigger");
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        final JobDataMap jobDataMap = new JobDataMap();
        cronTriggerFactoryBean.setJobDataMap(jobDataMap);
        final TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        cronTriggerFactoryBean.setTimeZone(timeZone);
        cronTriggerFactoryBean.setGroup(scheduledJobDetails.getGroupName());
        cronTriggerFactoryBean.setCronExpression(scheduledJobDetails.getCronExpression());
        cronTriggerFactoryBean.setPriority(scheduledJobDetails.getTaskPriority());
        cronTriggerFactoryBean.afterPropertiesSet();
        return cronTriggerFactoryBean.getObject();
    }

    private String getStackTraceAsString(final Throwable throwable) {
        final StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        final StringBuilder sb = new StringBuilder(throwable.toString());
        for (final StackTraceElement element : stackTraceElements) {
            sb.append("\n \t at ").append(element.getClassName()).append(".").append(element.getMethodName()).append("(")
                    .append(element.getLineNumber()).append(")");
        }
        return sb.toString();
    }

    private String getJobKeyAsString(final JobKey jobKey) {
        return jobKey.getName() + SchedulerServiceConstants.JOB_KEY_SEPERATOR + jobKey.getGroup();
    }

    private JobKey constructJobKey(final String Key) {
        final String[] keyParams = Key.split(SchedulerServiceConstants.JOB_KEY_SEPERATOR);
        final JobKey jobKey = new JobKey(keyParams[0], keyParams[1]);
        return jobKey;
    }

}

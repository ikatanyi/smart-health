/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.service;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SchedulerTriggerListener implements TriggerListener {
   Integer maxNumberOfRetries = 3;
        Integer maxIntervalBetweenRetries = 2;

    private final SchedulerService schedularService; 

    public SchedulerTriggerListener(SchedulerService schedularService) {
        this.schedularService = schedularService;
    }
  

    @Override
    public String getName() {
        return "Smarthealth Global Scheduler Trigger Listener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.debug("triggerFired() trigger={}, context={}", trigger, context);
    }

    @Override
    public boolean vetoJobExecution(final Trigger trigger, final JobExecutionContext context) { 
        final JobKey key = trigger.getJobKey();
        final String jobKey = key.getName() + SchedulerServiceConstants.JOB_KEY_SEPERATOR + key.getGroup();
        String triggerType = SchedulerServiceConstants.TRIGGER_TYPE_CRON;
        if (context.getMergedJobDataMap().containsKey(SchedulerServiceConstants.TRIGGER_TYPE_REFERENCE)) {
            triggerType = context.getMergedJobDataMap().getString(SchedulerServiceConstants.TRIGGER_TYPE_REFERENCE);
        }
      
        Integer numberOfRetries = 0;
        boolean vetoJob = false;
        while (numberOfRetries <= maxNumberOfRetries) {
            try {
                vetoJob = this.schedularService.processJobDetailForExecution(jobKey, triggerType);
                numberOfRetries = maxNumberOfRetries + 1;
            } catch (Exception exception) { // Adding generic exception as it depends on JPA provider
                log.warn("vetoJobExecution() not able to acquire the lock to update job running status at retry {} (of {}) for JobKey: {}",
                        numberOfRetries, maxNumberOfRetries, jobKey, exception);
                try {
                    Random random = new Random();
                    int randomNum = random.nextInt(maxIntervalBetweenRetries + 1);
                    Thread.sleep(1000 + (randomNum * 1000));
                    numberOfRetries = numberOfRetries + 1;
                } catch (InterruptedException e) {
                    log.error("vetoJobExecution() caught an InterruptedException", e);
                }
            }
        }
        if (vetoJob) {
            log.warn("vetoJobExecution() WILL veto the execution (returning vetoJob == true; the job's execute method will NOT be called); "
                    + "maxNumberOfRetries={}, tenant={}, jobKey={}, triggerType={}, trigger={}, context={}",
                    maxNumberOfRetries, 1, jobKey, triggerType, trigger, context);
        }
        return vetoJob;
    }

    @Override
    public void triggerMisfired(final Trigger trigger) {
        log.error("triggerMisfired() trigger={}", trigger);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        log.debug("triggerComplete() trigger={}, context={}, completedExecutionInstruction={}", trigger, context, triggerInstructionCode);
    }
}


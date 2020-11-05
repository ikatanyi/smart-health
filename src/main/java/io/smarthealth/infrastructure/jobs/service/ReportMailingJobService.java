/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.service;

import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class ReportMailingJobService {
    
    @CronTarget(jobName = JobName.EXECUTE_REPORT_MAILING_JOBS)
    public void executeReportMailingJobs() throws JobExecutionException {
        log.info("Mailing report triggered for now at: "+LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        //get a list of all admitted - discharge
        //for each patient get bed category
        //for bed category get the charges - recur
        //for each create a bill for the patient
        
    } 
    
}

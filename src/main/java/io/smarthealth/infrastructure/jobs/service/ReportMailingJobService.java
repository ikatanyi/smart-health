/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.service;

import io.smarthealth.infrastructure.jobs.domain.JobName;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportMailingJobService {
    
    private final VisitService visitService;
    
    @CronTarget(jobName = JobName.EXECUTE_REPORT_MAILING_JOBS)
    public void executeReportMailingJobs() throws JobExecutionException {
        log.info("Mailing report triggered for now at: "+LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        //get a list of all admitted - discharge
        //for each patient get bed category
        //for bed category get the charges - recur
        //for each create a bill for the patient
        //the ;ogic to close the close
        
        
    } 
    
     @CronTarget(jobName = JobName.AUTO_CHECK_OUT_PATIENT)
    public void checkOutpatientVisit(){
        List<Visit> visits = visitService.fetchAllVisitsSurpassed24hrs();
        for (Visit v : visits) {
            v.setStatus(VisitEnum.Status.CheckOut);
            v.setStopDatetime(LocalDateTime.now());
            visitService.createAVisit(v);
        }
    }
}

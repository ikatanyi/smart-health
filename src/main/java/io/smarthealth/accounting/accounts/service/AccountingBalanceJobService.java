/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.service;

import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import io.smarthealth.infrastructure.jobs.domain.JobName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class AccountingBalanceJobService {

    @CronTarget(jobName = JobName.ACCOUNTING_RUNNING_BALANCE_UPDATE)
    public void updateRunningBalance() {
        log.info("updating accounting running balances services triggerred");
    }

}

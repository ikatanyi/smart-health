/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.data;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class JobDetailData {

    private final Long jobId;
    private final String displayName;
    private final LocalDateTime nextRunTime;
    private final String initializingError;
    private final String cronExpression;
    private final boolean active;
    private final boolean currentlyRunning;
    private final JobDetailHistoryData lastRunHistory;

    public JobDetailData(final Long jobId, final String displayName, final LocalDateTime nextRunTime, final String initializingError,
            final String cronExpression, final boolean active, final boolean currentlyRunning, final JobDetailHistoryData lastRunHistory) {
        this.jobId = jobId;
        this.displayName = displayName;
        this.nextRunTime = nextRunTime;
        this.initializingError = initializingError;
        this.cronExpression = cronExpression;
        this.active = active;
        this.lastRunHistory = lastRunHistory;
        this.currentlyRunning = currentlyRunning;
    }
}

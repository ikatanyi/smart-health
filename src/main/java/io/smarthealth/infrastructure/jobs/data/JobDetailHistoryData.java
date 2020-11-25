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
public class JobDetailHistoryData {

    private final Long version;

    private final LocalDateTime jobRunStartTime;

    private final LocalDateTime jobRunEndTime;

    private final String status;

    private final String jobRunErrorMessage;

    private final String triggerType;

    private final String jobRunErrorLog;

    public JobDetailHistoryData(final Long version, final LocalDateTime jobRunStartTime, final LocalDateTime jobRunEndTime, final String status,
            final String jobRunErrorMessage, final String triggerType, final String jobRunErrorLog) {
        this.version = version;
        this.jobRunStartTime = jobRunStartTime;
        this.jobRunEndTime = jobRunEndTime;
        this.status = status;
        this.jobRunErrorMessage = jobRunErrorMessage;
        this.triggerType = triggerType;
        this.jobRunErrorLog = jobRunErrorLog;
    }
}

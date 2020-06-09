package io.smarthealth.infrastructure.jobs.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "job")
public class ScheduledJobDetail extends Identifiable {
   @Column(name = "name")
    private String jobName;
    @Column(name = "display_name")
    private String jobDisplayName;
    private String cronExpression;
    private LocalDateTime createTime;
    private Short taskPriority;
    private String groupName;
    private LocalDateTime previousRunStartTime;
    private LocalDateTime nextRunTime;
    private String jobKey;
    @Column(name = "initializing_errorlog", columnDefinition = "TEXT")
    private String errorLog;
    @Column(name = "is_active")
    private boolean activeSchedular;
    private boolean currentlyRunning;
    private boolean updatesAllowed;
    private Short schedulerGroup;
     @Column(name = "is_misfired")
    private boolean triggerMisfired;

    protected ScheduledJobDetail() {

    }
}

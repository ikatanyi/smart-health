package io.smarthealth.infrastructure.jobs.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "job_parameters")
public class JobParameter extends Identifiable {
    private Long jobId;
    private String parameterName;
    private String parameterValue;

    public JobParameter() {
    }

    public JobParameter(final Long jobId, final String parameterName, final String parameterValue) {
        this.jobId = jobId;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public static JobParameter getInstance(final Long jobId, final String parameterName, final String parameterValue) {
        return new JobParameter(jobId, parameterName, parameterValue);
    }
}

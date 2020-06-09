package io.smarthealth.infrastructure.jobs.annotation;

import io.smarthealth.infrastructure.jobs.service.JobName;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method to be picked while scheduling a cron jobs.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CronTarget {

    JobName jobName();
}

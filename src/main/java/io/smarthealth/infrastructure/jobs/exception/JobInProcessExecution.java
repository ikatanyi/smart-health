package io.smarthealth.infrastructure.jobs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JobInProcessExecution extends RuntimeException {

    public JobInProcessExecution(final String identifier) {
        super("job execution is in process for " + identifier);
    }

}

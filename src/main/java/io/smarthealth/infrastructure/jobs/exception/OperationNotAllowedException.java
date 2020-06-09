package io.smarthealth.infrastructure.jobs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException(final String jobNames) {
        super("Execution is in-process for jobs " + jobNames
                + "...., so update operations are not allowed at this moment");
    }
}

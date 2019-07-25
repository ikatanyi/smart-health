package io.smarthealth.infrastructure.exception;

import java.text.MessageFormat;
import org.springframework.http.HttpStatus;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class APIException extends RuntimeException {

    private final ApiError apiError;

    public APIException(final ApiError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }

    public static APIException badRequest(final String message, final Object... args) {
        return new APIException(
                new ApiError(HttpStatus.BAD_REQUEST, MessageFormat.format(message, args))
        );
    }

    public static APIException notFound(final String message, final Object... args) {
        return new APIException(
                new ApiError(HttpStatus.NOT_FOUND, MessageFormat.format(message, args))
        );
    }

    public static APIException conflict(final String message, final Object... args) {
        return new APIException(
                new ApiError(HttpStatus.CONFLICT, MessageFormat.format(message, args))
        );
    }

    public static APIException internalError(final String message, final Object... args) {

        return new APIException(
                new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, MessageFormat.format(message, args))
        );
    }

    public ApiError apiError() {
        return this.apiError;
    }

    @Override
    public String toString() {
        return "APIException{"
                + "apiError=" + apiError
                + '}';
    }
}

package io.smarthealth.common.utility;

import java.text.MessageFormat;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class APIException extends RuntimeException {

    private final ServiceError serviceError;

    public APIException(final ServiceError serviceError) {
        super(serviceError.getMessage());
        this.serviceError = serviceError;
    }
    
    public static APIException badRequest(final String message, final Object... args) {
        return new APIException(ServiceError
                .create(400)
                .message(MessageFormat.format(message, args))
                .build());
    }

    public static APIException notFound(final String message, final Object... args) {
        return new APIException(ServiceError
                .create(404)
                .message(MessageFormat.format(message, args))
                .build());
    }

    public static APIException conflict(final String message, final Object... args) {

        return new APIException(ServiceError
                .create(409)
                .message(MessageFormat.format(message, args))
                .build());
    }

    public static APIException internalError(final String message, final Object... args) {
        return new APIException(ServiceError
                .create(500)
                .message(MessageFormat.format(message, args))
                .build());
    }

    public ServiceError serviceError() {
        return this.serviceError;
    }

    @Override
    public String toString() {
        return "ServiceException{"
                + "serviceError=" + serviceError
                + '}';
    }
}

package io.smarthealth.infrastructure.exception;

import java.text.MessageFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.smarthealth.accounting.billing.data.LimitExceedingResponse;
import io.smarthealth.accounting.billing.data.LimitResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class APIException extends RuntimeException {

    private ApiError apiError;


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

    public static APIException limitExceedResponse(LimitExceedingResponse response) {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = "";
        try {
            json = ow.writeValueAsString(response);
        } catch (Exception e) {
            throw APIException.internalError(e.getMessage());
        }

        return new APIException(
                new ApiError(HttpStatus.OK, json)
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

package io.smarthealth.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 *
 * @author Kelsas
 */
@Data
//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
//@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
@JsonInclude(Include.NON_NULL)
public class ApiError {
    private String title;
    private int status;
    @JsonIgnore
     private HttpStatus httpStatus;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
//    private LocalDateTime timestamp;
    private String details;
    private String debugMessage;
    private List<ApiSubError> errors;

    private ApiError() {
//        timestamp = LocalDateTime.now();
    }

    ApiError(HttpStatus status) {
        this();
        this.httpStatus = status;
        this.status=status.value();
        this.title=status.getReasonPhrase();
    }

    ApiError(HttpStatus status, Throwable ex) {
        this();
        this.httpStatus = status;
        this.details = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
         this.status=status.value();
        this.title=status.getReasonPhrase();
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.httpStatus = status;
        this.details = message;
        this.debugMessage = ex.getLocalizedMessage();
         this.status=status.value();
        this.title=status.getReasonPhrase();
    }
    
    ApiError(HttpStatus status, String message) {
        this();
        this.httpStatus = status;
        this.details = message;
         this.status=status.value();
        this.title=status.getReasonPhrase();
    }

    private void addSubError(ApiSubError subError) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(subError);
    }

    private void addValidationError(String field, String message) {
        addSubError(new ApiValidationError(field, message));
    }

    private void addValidationError(String message) {
        addSubError(new ApiValidationError(message));
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError( 
                fieldError.getField(), 
                fieldError.getDefaultMessage());
    }

    void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }

    private void addValidationError(ObjectError objectError) {
        this.addValidationError(
                objectError.getObjectName(),
                objectError.getDefaultMessage());
    }

    void addValidationError(List<ObjectError> globalErrors) {
        globalErrors.forEach(this::addValidationError);
    }

    /**
     * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation fails.
     *
     * @param cv the ConstraintViolation
     */
    private void addValidationError(ConstraintViolation<?> cv) {
        this.addValidationError( 
                ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), 
                cv.getMessage());
    }

    void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
        constraintViolations.forEach(this::addValidationError);
    }


    abstract class ApiSubError {

    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    class ApiValidationError extends ApiSubError {
//        private String object;
        private String field;
//        private Object rejectedValue;
        private String message;

        ApiValidationError(String message) {
//            this.object = object;
            this.message = message;
        }
    }
}

class LowerCaseClassNameResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object value) {
        return value.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}

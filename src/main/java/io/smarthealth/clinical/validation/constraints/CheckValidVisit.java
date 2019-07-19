package org.smarthealth.clinical.validation.constraints;
 
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import org.smarthealth.clinical.validation.CheckVisitNumber; 

/**
 * The annotated string must not be null, must have a minimum length of 2, and
 * must be equals to itself URL-encoded.
 *  
 * @author Kelsas
 */
@SuppressWarnings("unused")
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckVisitNumber.class)
public @interface CheckValidVisit {

    String message() default "Invalid Visit Number.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxLength() default 32;

    boolean optional() default false;
}

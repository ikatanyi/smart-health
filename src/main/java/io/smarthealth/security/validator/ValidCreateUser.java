package io.smarthealth.security.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Kelsas
 */
@Target(ElementType.TYPE) // <1>
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CreateUserConstraintValidator.class}) //<2>
public @interface ValidCreateUser {

    String message() default "Invalid user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

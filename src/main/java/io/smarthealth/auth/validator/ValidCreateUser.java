package io.smarthealth.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

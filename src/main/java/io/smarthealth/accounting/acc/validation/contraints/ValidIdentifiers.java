 package io.smarthealth.accounting.acc.validation.contraints;

 import io.smarthealth.accounting.acc.validation.CheckIdentifiers;
 import java.lang.annotation.Documented;
 import static java.lang.annotation.ElementType.*;
 import java.lang.annotation.Retention;
 import static java.lang.annotation.RetentionPolicy.RUNTIME;
 import java.lang.annotation.Target;
 import javax.validation.Constraint;
 import javax.validation.Payload;

/**
 * The annotated string must not be null, must have a minimum length of 2, and must be
 * equals to itself URL-encoded.
 *
 * @author Kelsas
 */
@SuppressWarnings("unused")
@Target({ FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckIdentifiers.class)
public @interface ValidIdentifiers {
  String message() default "Invalid Smarthealth identifier.";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };

  int maxLength() default 32;
  boolean optional() default false;
}

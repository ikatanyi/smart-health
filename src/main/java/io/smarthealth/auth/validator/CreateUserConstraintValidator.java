package io.smarthealth.auth.validator;

import io.smarthealth.auth.data.UserRequest;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.domain.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
@Slf4j
public class CreateUserConstraintValidator implements ConstraintValidator<ValidCreateUser, UserRequest> {

    private final UserRepository userRepository;

    public CreateUserConstraintValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(UserRequest userRequest, ConstraintValidatorContext context) {

        Optional<User> user = userRepository.findByUsernameOrEmail(userRequest.getUsername(), userRequest.getEmail());
        
        if (user.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An account for that username/email already exists. Please enter a different username.").addConstraintViolation();
            return false;
        }

        return true;

    }

}

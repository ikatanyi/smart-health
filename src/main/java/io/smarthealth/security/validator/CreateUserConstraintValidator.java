package io.smarthealth.security.validator;
 
import io.smarthealth.security.data.SignUpRequest;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.UserRepository;
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
public class CreateUserConstraintValidator implements ConstraintValidator<ValidCreateUser, SignUpRequest> {

    private final UserRepository userRepository;

    public CreateUserConstraintValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(SignUpRequest userRequest, ConstraintValidatorContext context) {

        Optional<User> user = userRepository.findByUsernameOrEmail(userRequest.getUsername(), userRequest.getEmail());

        if (user.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An account for that username/email already exists. Please enter a different username.").addConstraintViolation();
            return false;
        }

        return true;

    }

}

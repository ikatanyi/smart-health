package io.smarthealth.security.service;

import io.smarthealth.events.data.PatientResultEvent;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.domain.PasswordResetToken;
import io.smarthealth.security.domain.PasswordTokenRepository;
import io.smarthealth.security.domain.Role;
import io.smarthealth.security.domain.RoleRepository;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.UserRepository;
import io.smarthealth.security.domain.specification.UserSpecification;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author Kelsas
 */
@Service
public class UserService {

//    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
//    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, PasswordTokenRepository passwordTokenRepository) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Optional<User> findUserByUsernameOrEmail(String username) {
        return userRepository.findByUsernameOrEmail(username, username);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        //{bcrypt}
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public Optional<User> findUserByEmail(final String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    public void changeUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public String validatePasswordResetToken(long id, String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            return "invalidToken";
        }
        Duration duration = Duration.between(passToken.getExpiryDate(), LocalDateTime.now());
        if (Math.abs(duration.toMinutes()) <= 0) {
            return "expired";
        }

        final User user = passToken.getUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    public Page<Role> getAuthorities(Pageable pgbl) {
        return roleRepository.findAll(pgbl);
    }

    public Page<User> searchAllUsers(String search, Pageable page) {
        Specification<User> spec = UserSpecification.createSpecification(search);
        return userRepository.findAll(spec, page);
    }

    @Transactional
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> APIException.notFound("User with id {0} Not Found", id));
    }

//    public void addEmitter(final SseEmitter emitter) {
//        emitters.add(emitter);
//    }
//
//    public void removeEmitter(final SseEmitter emitter) {
//        emitters.remove(emitter);
//    }
////Will convert this to an event so that it listens to this
//
//    @Async
//    @Scheduled(fixedRate = 5000)
//    public void doNotify() throws IOException {
//        //get the notification for this niggar
//        List<SseEmitter> deadEmitters = new ArrayList<>();
//        emitters.forEach(emitter -> {
//            try {
//                emitter.send(SseEmitter.event()
//                        .data(DATE_FORMATTER.format(new Date()) + " : " + UUID.randomUUID().toString()));
//            } catch (Exception e) {
//                deadEmitters.add(emitter);
//            }
//        });
//        emitters.removeAll(deadEmitters);
//    }
    
    

    //sign up { email | password | name | username | captchaResponse }
    //Resend Verification mail
    //verify user
    //forgot password
    //reset password
    //user profile
    //update user
    //change password
    //requestung for changing email
    //change email
//    public String forgotPasswordRequest(String email) {
//        Optional<User> optional = userRepository.findByEmailI(email);
//
//        if (optional.isPresent()) {
//            // Generate random 36-character string token for reset password 
//            User user = optional.get();
//            user.setResetToken(UUID.randomUUID().toString());
//            userRepository.save(user);
//            String appUrl = "http://localhost:8200/api/users/login/reset?token=" + user.getResetToken();
//            ApplicationMailData data = new ApplicationMailData();
//            data.setTo(email);
//            data.setSubject("Password Reset Request");
//            data.setBody("To reset your password, click the link below:\n" + appUrl);
//            mailSender.send(data);
//            return "A password reset link has been sent to " + email;
//        } else {
//            throw APIException.notFound("We didn't find an account for that e-mail address.. {0}", email);
//        }
//    }
//
//    public String resetPassword(String token, String password) {
//        Optional<User> user = userRepository.findByResetToken(token);
//
//        if (user.isPresent()) {
//            User resetUser = user.get();
//            // Set new password	          
//            resetUser.setPassword(passwordEncoder.encode(password));
//            // Set the reset token to null so it cannot be used again
//            resetUser.setResetToken(null);
//            // Save user
//            userRepository.save(resetUser);
//            return "You have successfully reset your password.  You may now login.";
//            //"redirect:login"
//        } else {
//            throw APIException.badRequest("Oops!  This is an invalid password reset link.");
//        }
//    }
}

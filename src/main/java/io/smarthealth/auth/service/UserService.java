/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.service;

import io.smarthealth.auth.data.UserData;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.domain.UserRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.mail.ApplicationMailData;
import io.smarthealth.infrastructure.mail.MailSender;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            MailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

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
    public UserData createUser(UserData userData) {
        User user = UserData.map(userData);
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setVerified(false);
        // we need to send a verification email
        User savedUser = userRepository.save(user);
        //check if the use exists
        return UserData.map(savedUser);
    }

    public String forgotPasswordRequest(String email) {
        Optional<User> optional = userRepository.findByEmailI(email);

        if (optional.isPresent()) {
            // Generate random 36-character string token for reset password 
            User user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());
            userRepository.save(user);
            String appUrl = "http://localhost:8200/api/users/login/reset?token=" + user.getResetToken();
            ApplicationMailData data = new ApplicationMailData();
            data.setTo(email);
            data.setSubject("Password Reset Request");
            data.setBody("To reset your password, click the link below:\n" + appUrl);
            mailSender.send(data);
            return "A password reset link has been sent to " + email;
        } else {
            throw APIException.notFound("We didn't find an account for that e-mail address.. {0}", email);
        }
    }

    public String resetPassword(String token, String password) {
        Optional<User> user = userRepository.findByResetToken(token);

        if (user.isPresent()) {
            User resetUser = user.get();
            // Set new password	          
            resetUser.setPassword(passwordEncoder.encode(password));
            // Set the reset token to null so it cannot be used again
            resetUser.setResetToken(null);
            // Save user
            userRepository.save(resetUser);
            return "You have successfully reset your password.  You may now login.";
            //"redirect:login"
        } else {
            throw APIException.badRequest("Oops!  This is an invalid password reset link.");
        }
    }

}

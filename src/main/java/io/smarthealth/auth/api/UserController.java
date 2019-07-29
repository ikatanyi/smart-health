package io.smarthealth.auth.api;

import io.smarthealth.auth.data.PasswordDto;
import io.smarthealth.auth.data.UserData;
import io.smarthealth.auth.data.UserRequest;
import io.smarthealth.auth.domain.Role;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.service.UserService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailSender;
import io.smarthealth.infrastructure.utility.GenericResponse;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService service;
    private final ModelMapper modelMapper;
    private final MailSender mailSender;

    public UserController(UserService service, ModelMapper modelMapper, MailSender mailSender) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
    }

    @GetMapping("/users/me")
    public UserData currentUser(Authentication authentication) {
        String username = authentication.getName();
        System.err.println("current user ... " + authentication.getName());
        Optional<User> user = service.findUserByUsernameOrEmail(username);
        if (user.isPresent()) {
            return convertToData(user.get());
        } else {
            throw APIException.notFound("Authentication Error Current User Not Found");
        }

    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {

        List<Role> roles = new ArrayList<>();
        for (String roleData : userRequest.getRoles()) {
            Role role = service.findRoleByName(roleData)
                    .orElseThrow(
                            () -> APIException.notFound("No Role exisit with the name {0}", roleData)
                    );
            roles.add(role);
        }
        User user = new User(
                userRequest.getEmail(),
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getName(),
                roles
        );
        User result = service.saveUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(convertToData(result));
    }

    @GetMapping("/users/{username}")
    public UserData getUserProfile(@PathVariable(value = "username") String username) {
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username or email {0} not found.... ", username));
        return convertToData(user);
    }

    // Reset password 
    @ResponseBody
    @PostMapping("/user/resetPassword")
    public ResponseEntity<?> resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = service.findUserByEmail(userEmail)
                .orElseThrow(() -> APIException.notFound("No user with email {0} Found", userEmail));

        final String token = UUID.randomUUID().toString();
        service.createPasswordResetTokenForUser(user, token);
        //email the
        //this should be the page wot
        final String url = getAppUrl(request) + "/api/users/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = "Reset Password" + " \r\n" + url;
        mailSender.send(EmailData.of(user.getEmail(), "Reset Password", message));

        return ResponseEntity.ok(new GenericResponse("You should receive an Password Reset Email shortly"));
    }

     @GetMapping(value = "/users/changePassword")
    public ResponseEntity<?> showChangePasswordPage(@RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = service.validatePasswordResetToken(id, token);
        if (result != null) {
            if(result.equals("expired")){
                throw APIException.badRequest("Your registration token has expired. Please register again.");
            }
            if(result.equals("invalidToken")){
                throw APIException.badRequest("Invalid token");
            }
        }
         URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/updatePassword")
                .buildAndExpand().toUri();
        return ResponseEntity.ok(new GenericResponse("Token Validated Success. \n Change Password : "+location.toString()));
    }
    // change user password
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse changeUserPassword(Authentication authentication, @Valid PasswordDto passwordDto) {
        
        String username = authentication.getName();
        log.info("Changing Password ... "+username);
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("You need to be logged in to change your password"));

        if (!service.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw APIException.badRequest("Invalid Old Password");
        }
        service.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse("Password updated successfully");
    }

    @GetMapping("/users")
    public Page<?> fetchAllUsers(Pageable page) {

//        final ContentPage<User> userPage = new ContentPage();
//        userPage.setTotalPages(patientEntities.getTotalPages());
//        userPage.setTotalElements(patientEntities.getTotalElements());
//
//        if (patientEntities.getSize() > 0) {
//            final ArrayList<Patient> patients = new ArrayList<>(patientEntities.getSize());
//            userPage.setContents(patients);
//            patientEntities.forEach(patientEntity -> patients.add(Patient.map(patientEntity)));
//        }
        return service.findAllUsers(page);
    }

    private UserData convertToData(User user) {
        UserData userData = modelMapper.map(user, UserData.class);
        if (!user.getRoles().isEmpty()) {
            List<String> roles = new ArrayList<>();

            user.getRoles().forEach((role) -> {
                roles.add(role.getName());
            });
            userData.setRoles(roles);
        }
        userData.setPassword("**************");
        return userData;
    }

    private User convertToEntity(UserData userData) throws ParseException {
        User user = modelMapper.map(userData, User.class);
        //then we find the roles with it's IP
        List<Role> roles = new ArrayList<>();
        for (String roleData : userData.getRoles()) {
            Role role = service.findRoleByName(roleData)
                    .orElseThrow(
                            () -> APIException.notFound("No Role exisit with the name {0}", roleData)
                    );
            roles.add(role);
        }
        user.setRoles(roles);
        return user;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
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
}

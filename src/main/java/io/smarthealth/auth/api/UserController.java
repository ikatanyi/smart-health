package io.smarthealth.auth.api;

import io.smarthealth.auth.data.PasswordDto;
import io.smarthealth.auth.data.UserData;
import io.smarthealth.auth.data.UserRequest;
import io.smarthealth.auth.domain.Role;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.service.UserService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailService;
import io.smarthealth.infrastructure.utility.GenericResponse;
import io.swagger.annotations.Api;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService service;
    private final ModelMapper modelMapper;
    private final MailService mailSender;

    public UserController(UserService service, ModelMapper modelMapper, MailService mailSender) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
    }

    @GetMapping("/users/me")
    public UserData currentUser(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = service.findUserByUsernameOrEmail(username);
        if (user.isPresent()) {
            return convertToData(user.get());
        } else {
            throw APIException.notFound("Authentication Error Current User Not Found");
        }

    }

    @GetMapping("/users/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
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
    public UserData getUserProfile(@PathVariable(value = "username") String username, OAuth2Authentication authentication) {
        String auth = (String) authentication.getUserAuthentication().getPrincipal();
        log.info("Logged Users : " + auth);

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
            if (result.equals("expired")) {
                throw APIException.badRequest("Your registration token has expired. Please register again.");
            }
            if (result.equals("invalidToken")) {
                throw APIException.badRequest("Invalid token");
            }
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/updatePassword")
                .buildAndExpand().toUri();
        return ResponseEntity.ok(new GenericResponse("Token Validated Success. \n Change Password : " + location.toString()));
    }

    // change user password
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse changeUserPassword(Authentication authentication, @Valid PasswordDto passwordDto) {

        String username = authentication.getName();
        log.info("Changing Password ... " + username);
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("You need to be logged in to change your password"));

        if (!service.checkIfValidOldPassword(user, passwordDto.getCurrentPassword())) {
            throw APIException.badRequest("Invalid Currrent Password");
        }
        service.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse("Password updated successfully");
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserData>> getAllUsers(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<UserData> page = service.findAllUsers(pageable).map(u -> convertToData(u));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/users/authorities")
    @PreAuthorize("hasAuthority('role_admin')")
    public Page<Role> getAuthorities(Pageable page, OAuth2Authentication authentication) {
        String auth = (String) authentication.getUserAuthentication().getPrincipal();
        System.err.println("THis is the user : " + auth);
        authentication.getAuthorities().forEach(d -> {
            System.err.println(d.getAuthority());
        });
        return service.getAuthorities(page);
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

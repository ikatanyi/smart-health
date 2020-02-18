package io.smarthealth.security.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailService;
import io.smarthealth.security.data.ApiResponse;
import io.smarthealth.security.data.PasswordData;
import io.smarthealth.security.data.SignUpRequest;
import io.smarthealth.security.data.UserData;
import io.smarthealth.security.domain.Role;
import io.smarthealth.security.domain.RoleName;
import io.smarthealth.security.domain.RoleRepository;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.UserRepository;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.MultiValueMap;
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
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@Api
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MailService mailSender;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
//String email, String username, String password, String name
        // Creating user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getUsername(),
                signUpRequest.getPassword(),
                signUpRequest.getName());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> APIException.internalError("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable(value = "username") String username, OAuth2Authentication authentication) {
        String auth = (String) authentication.getUserAuthentication().getPrincipal();

        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username or email {0} not found.... ", username));
        return ResponseEntity.ok(user.toData());
    }
 
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<UserData> page = userService.findAllUsers(pageable).map(u -> u.toData());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
     // Reset password 
    @ResponseBody
    @PostMapping("/user/resetPassword")
    public ResponseEntity<?> resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail)
                .orElseThrow(() -> APIException.notFound("No user with email {0} Found", userEmail));

        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token); 
        
        final String url = getAppUrl(request) + "/api/users/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = "Reset Password" + " \r\n" + url;
        mailSender.send(EmailData.of(user.getEmail(), "Reset Password", message));

        return ResponseEntity.ok(new ApiResponse(true, "You should receive an Password Reset Email shortly"));
    }
    @GetMapping(value = "/users/changePassword")
    public ResponseEntity<?> showChangePasswordPage(@RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = userService.validatePasswordResetToken(id, token);
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
        return ResponseEntity.ok(new ApiResponse(true, "Token Validated Success. \n Change Password : " + location.toString()));
    }

    // change user password
    @ResponseBody
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.POST)  
    public ResponseEntity<?> changeUserPassword(Authentication authentication, @Valid PasswordData passwordDto) {

        String username = authentication.getName();
        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("You need to be logged in to change your password"));

        if (!userService.checkIfValidOldPassword(user, passwordDto.getCurrentPassword())) {
            throw APIException.badRequest("Invalid Currrent Password");
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());

        return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully"));
    }

    

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}

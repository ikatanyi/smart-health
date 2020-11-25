package io.smarthealth.security.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notification.data.EmailData;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.infrastructure.utility.PassayPassword;
import io.smarthealth.notification.data.NotificationResponse;
import io.smarthealth.security.data.ApiResponse;
import io.smarthealth.security.data.PasswordData;
import io.smarthealth.security.data.PermissionData;
import io.smarthealth.security.data.UserData;
import io.smarthealth.security.data.UserPermission;
import io.smarthealth.security.domain.Permission;
import io.smarthealth.security.domain.Role;
import io.smarthealth.security.domain.RoleRepository;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.UserRepository;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.smarthealth.notification.service.EmailerService;

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
    private final EmailerService mailSender;

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('create_users')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserData data) {
        if (containsWhitespace(data.getUsername())) {
            throw APIException.badRequest("Username should not have spaces!");
        }
        if (userRepository.existsByUsername(data.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(data.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
//String email, String username, String password, String name
//generate password
        String password = PassayPassword.generatePassayPassword();

        // Creating user's account
        User user = new User(data.getEmail(),
                data.getUsername(),
                password,
                data.getName());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        /*
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> APIException.internalError("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
         */
        if (!data.getRoles().isEmpty()) {
            Set<Role> userRoles = new HashSet<>();
            for (String role : data.getRoles()) {
                Role userRole = roleRepository.findByName(role)
                        .orElseThrow(() -> APIException.internalError("User Role not found."));
                userRoles.add(userRole);
            }
            user.setRoles(userRoles);
        }
        User result = userRepository.save(user);
//send welcome message to the new system user
        mailSender.send(EmailData.of(user.getEmail(), "User Account", "<b>Welcome</b> " + user.getName().concat(". Your login credentials is <br/> username : " + user.getUsername() + "<br/> password : " + password)));

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/auth/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<UserData> list = userService.searchAllUsers(search, pageable)
                .map(u -> u.toData());
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);

        Pager<List<UserData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Users");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
//        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{username}")
    @PreAuthorize("hasAuthority('edit_users')")
    public ResponseEntity<?> updateUserProfile(@PathVariable(value = "username") String username, @Valid @RequestBody final UserData data) {
        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username or email {0} not found.... ", username));
        user.setEmail(data.getEmail());
        user.setName(data.getName());

        if (!data.getRoles().isEmpty()) {
            Set<Role> userRoles = new HashSet<>();
            for (String role : data.getRoles()) {
                Role userRole = roleRepository.findByName(role)
                        .orElseThrow(() -> APIException.internalError("User Role not found."));
                userRoles.add(userRole);
            }
            user.setRoles(userRoles);
        }
        user.setUsername(data.getUsername());
        user.setEnabled(data.isEnabled());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser.toData());
    }

    @GetMapping("/users/{username}")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<?> getUserProfile(@PathVariable(value = "username") String username) {

        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username or email {0} not found.... ", username));

        return ResponseEntity.ok(user.toData());
    }

    @GetMapping("/users/{username}/permissions")
    public ResponseEntity<?> getPermissions(@PathVariable(value = "username") String username) {

        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username or email {0} not found.... ", username));

        ArrayList<UserPermission> list = new ArrayList<>();

        user.getRoles().stream()
                .forEach(role -> {
                    Map<String, List<Permission>> permissions = role.getPermissions().stream()
                            .collect(groupingBy(Permission::getPermissionGroup));

                    permissions.forEach((k, v) -> {
                        List<PermissionData> data = v.stream().map(x -> x.toData()).collect(Collectors.toList());
                        list.add(new UserPermission(k, data));
                    });

                });
        return ResponseEntity.ok(list);
    }

    // Reset password 
    @ResponseBody
    @PostMapping("/user/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail)
                .orElseThrow(() -> APIException.notFound("No user with email {0} Found", userEmail));

//        final String token = UUID.randomUUID().toString();
        String password = PassayPassword.generatePassayPassword();
        userService.createPasswordResetTokenForUser(user, password);

//        final String url = getAppUrl(request) + "/api/auth/users/changePassword?id=" + user.getId() + "&token=" + token;
//        final String message = "Reset Password" + " \r\n" + url;
//        mailSender.send(EmailData.of(user.getEmail(), "Reset Password", message));
        mailSender.send(EmailData.of(user.getEmail(), "Account Password Reset", "<b>Dear</b> " + user.getName().concat(". Your password reset : " + password + " . Login and change the password.")));

        user.setPassword(passwordEncoder.encode(password));
        user.setFirstTimeLogin(true);
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "You should receive a temporarly password on your registered email shortly"));
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
                .fromCurrentContextPath().path("/api/auth/users/updatePassword")
                .buildAndExpand().toUri();
        return ResponseEntity.ok(new ApiResponse(true, "Token Validated Success. \n Change Password : " + location.toString()));
    }

    // change user password
    @ResponseBody
    @PreAuthorize("hasAuthority('create_users')")
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.POST)
    public ResponseEntity<?> changeUserPassword(Authentication authentication, @Valid PasswordData passwordDto) {

        String username = authentication.getName();
        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("You need to be logged in to change your password"));

        if (!userService.checkIfValidOldPassword(user, passwordDto.getCurrentPassword())) {
            throw APIException.badRequest("Invalid Currrent Password");
        }
        user.setFirstTimeLogin(false);

        userService.changeUserPassword(user, passwordDto.getNewPassword());

        return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully"));
    }

    boolean containsWhitespace(String str) {
        return str.matches(".*\\s.*");
    }

    @GetMapping("/get-notifications")
    public ResponseEntity<?> getUserNotifications(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findUserByUsernameOrEmail(username).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(new NotificationResponse(Boolean.FALSE, "User is empty, No notifications"));
        }

        return ResponseEntity.ok(userService.getUserNotification(user));

    }
}

package io.smarthealth.auth.api;

import io.smarthealth.auth.config.ApplicationUserDetails;
import io.smarthealth.auth.data.UserData;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.service.UserService;
import io.smarthealth.infrastructure.exception.APIException; 
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserData currentUser(@AuthenticationPrincipal ApplicationUserDetails userDetails) {
        log.debug("Get Current User Details ... "); 
        System.err.println("Current Principal ... "+userDetails.getUsername());
        User user = service.getUser(userDetails.getId())
                .orElseThrow(() -> APIException.notFound("User Details Not Found ... "));
         
        System.err.println(user.getEmail());
       return UserData.map(user);
    }
}

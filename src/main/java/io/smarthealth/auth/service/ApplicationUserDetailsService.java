package io.smarthealth.auth.service;

import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(()
                        -> new UsernameNotFoundException("User not found with username or email : " + username)
                );
//        ApplicationUserDetails appUser = new ApplicationUserDetails(user);
        new AccountStatusUserDetailsChecker().check(user);

        return user;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.config;

import io.smarthealth.security.data.ActiveUserStore;
import io.smarthealth.security.domain.UserRepository;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component("myAuthenticationSuccessHandler")
public class AppUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
 
    @Autowired
    ActiveUserStore activeUserStore;
    @Autowired
     UserRepository userRepository;
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
      HttpServletResponse response, Authentication authentication)  throws IOException {
        System.err.println(authentication);
        HttpSession session = request.getSession(false);
        if (session != null) {
            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            session.setAttribute("user", user);
             userRepository.updateLastLogin(authentication.getName());
        }
    }
}

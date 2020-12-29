package io.smarthealth.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component("customBasicAuthFilter")
public class CustomBasicAuthFilter extends BasicAuthenticationFilter {
    private AuditTrailService auditTrailService;
    @Autowired
    public CustomBasicAuthFilter(AuthenticationManager authenticationManager, AuditTrailService auditTrailService) {
        super(authenticationManager);
        this.auditTrailService = auditTrailService;
    }

    protected void onSuccessfulAuthentication(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Authentication authResult) {
        // Do what you want here
        System.out.println("=================LoggedIn=================");
        auditTrailService.saveAuditTrail("Security", "Logged in");
        System.out.println("=================LoggedIn=================");
    }
}

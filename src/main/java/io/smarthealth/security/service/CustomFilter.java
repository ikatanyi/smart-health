/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.smarthealth.security.service;

import io.smarthealth.security.domain.AuditTrail;
import io.smarthealth.security.domain.AuditTrailRepository;
import io.smarthealth.security.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 * @author kent
 */
@Component
@RequiredArgsConstructor
public class CustomFilter extends GenericFilterBean {

private final AuditTrailRepository auditTrailRepository;

    @Override
    public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String username="";
            Object principal = authentication.getPrincipal();
            if(principal instanceof UserDetails)
                username= ((UserDetails) principal).getUsername();
            else
                username=principal.toString();
            AuditTrail auditTrail  = new AuditTrail();
            auditTrail.setDescription("Logged In");
//            auditTrail.setLastModifiedBy(username);
//            auditTrail.setCreatedBy(username);
            auditTrail.setName("Login");
            if(!username.equals("anonymousUser"))
               auditTrailRepository.save(auditTrail);
        }
        chain.doFilter(request, response);
    }
}

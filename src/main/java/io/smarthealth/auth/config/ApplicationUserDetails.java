/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.config;

import io.smarthealth.auth.domain.User;

/**
 *
 * @author Kelsas
 */
public class ApplicationUserDetails extends org.springframework.security.core.userdetails.User {

    private final Long id;

    public ApplicationUserDetails(User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        this.id = user.getId();
    }

    public Long getId() {
        return id;
    }

}

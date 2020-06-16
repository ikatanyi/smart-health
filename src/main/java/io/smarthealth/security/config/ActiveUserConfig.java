/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.config;

import io.smarthealth.security.data.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
public class ActiveUserConfig {

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
}

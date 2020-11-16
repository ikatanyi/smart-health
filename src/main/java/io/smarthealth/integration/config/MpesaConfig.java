/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 *
 * @author Kelsas
 */
@Slf4j
@EnableOAuth2Client
@Configuration
public class MpesaConfig {

    @Bean
    @ConfigurationProperties("smarthealth.oauth2.client")
    protected OAuth2ProtectedResourceDetails clientClientCredsRestTemplate() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    protected OAuth2RestTemplate oauth2restTemplate(OAuth2ProtectedResourceDetails resourceDetails) {
        AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
        
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(accessTokenRequest));
    }
}

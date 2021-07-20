package io.smarthealth.security.config;

import io.smarthealth.security.service.CustomAccessDeniedHandler;
import io.smarthealth.security.service.CustomAuthenticationEntryPoint;
import io.smarthealth.security.service.CustomFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


/**
 *
 * @author Kelsas
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomFilter customFilter;

    public ResourceServerConfiguration(CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomFilter customFilter) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customFilter = customFilter;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("api");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(Public_Matchers).permitAll()
                .antMatchers(HttpMethod.GET, GET_Public_Matchers).permitAll()
                .antMatchers(HttpMethod.GET,"/api/report/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(customFilter, BasicAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(new CustomAccessDeniedHandler());


    }

     private final String[] Public_Matchers = {
            "/",
            "/favicon.ico",
            "/**/*.png",
            "/**/*.gif",
            "/**/*.svg",
            "/**/*.jpg",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js", 
            "/api/v1/smartpayments/**",
            "/api/auth/user/resetPassword/**",
            "/v2/api-docs/**", "/configuration/ui/**", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html/**", "/webjars/**"
    };
     
    // Matchers available for public via HTTP GET
    private final String[] GET_Public_Matchers = {
        "/auth/**",
        "/api/downloadFile/**",
        "/api/report/**",
        "/v2/api-docs/**",
        "/health**",
        "/metrics**",
        "/loggers**",
        "/actuator/**",
        "/swagger-ui.html/**"
    };
}

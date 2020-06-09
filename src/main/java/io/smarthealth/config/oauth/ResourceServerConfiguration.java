package io.smarthealth.config.oauth;

import io.kelsas.accounting.security.service.CustomAccessDeniedHandler;
import io.smarthealth.security.service.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 *
 * @author Kelsas
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public ResourceServerConfiguration(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
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
//                .authorizeRequests()
//                .antMatchers("/app/**", "/ws/**", "/notifications/**").permitAll()
//                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**").permitAll()
                //               .antMatchers("/api/users/**").hasAuthority("ADMIN")
                //                .antMatchers(HttpMethod.GET, "/v2/api-docs/**", "/swagger-ui.html*", "/company/logo*").permitAll()
                .authorizeRequests().antMatchers(Public_Matchers).permitAll()
                .antMatchers(HttpMethod.GET, GET_Public_Matchers).permitAll()
                .anyRequest().authenticated()
//                .antMatchers("/api/**").authenticated()
                .and()
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
            "/app/**",
            "/ws/**", 
            "/notifications/**",
            "/v2/api-docs/**", "/configuration/ui/**", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html/**", "/webjars/**"
    };
     
    // Matchers available for public via HTTP GET
    private final String[] GET_Public_Matchers = {
        "/auth/**",
        "/api/downloadFile/**",
        "/api/report/**",
        "/v2/api-docs/**",
        "/swagger-ui.html/**"
    };
}

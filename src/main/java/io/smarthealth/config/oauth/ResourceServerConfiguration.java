package io.smarthealth.config.oauth;

import io.kelsas.accounting.security.service.CustomAccessDeniedHandler;
import io.smarthealth.security.service.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
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

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()  
//                .antMatchers(HttpMethod.OPTIONS, "/api/**","/company/**").permitAll()
//                //.antMatchers("/api/user/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE").and()
//                .and()
//                .antMatcher("/api/**").authorizeRequests()
//                .antMatchers(HttpMethod.GET, /*"/api/users",*/ "/v2/api-docs/**", "/swagger-ui.html*", "/company/logo*").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/auth/signup").permitAll() /* This end-point should be dedicated to a scenario where this sytem is provided as a service. Different companies(hospitals) can signup and create their account*/
//                .antMatchers("/sse/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
//                .accessDeniedHandler(new CustomAccessDeniedHandler());
//    }
//    httpSecurity.authorizeRequests()
//                    .antMatchers(HttpMethod.GET)                  
//                    .permitAll() // Allow all GET requests to go unauthenticated
//                    .antMatchers(allowedResources)                  
//                    .permitAll() // Allow all requests to go unauthenticated for the specified paths
//                    .antMatchers(protectedResources).hasRole(USER)
//                    .antMatchers(adminResources).hasRole(ADMIN)
//                    .anyRequest().authenticated(); // Authenticate all other request paths
    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers(HttpMethod.GET).permitAll()
//                .antMatchers("/sse/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
//                .accessDeniedHandler(new CustomAccessDeniedHandler());

//  http
//               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//               .and()
//               .authorizeRequests()
//               .antMatchers("/sse/**").permitAll()
//               .antMatchers("/api/glee/**").hasAnyAuthority("ADMIN", "USER")
//               .antMatchers("/api/users/**").hasAuthority("ADMIN")
//               .antMatchers("/api/**").authenticated()
//               .anyRequest().authenticated()
//               .and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(new CustomAccessDeniedHandler());
        
        http
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .and()
               .authorizeRequests()
               .antMatchers("/app/**","/ws/**","/notifications/**").permitAll()
               .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**").permitAll()
//               .antMatchers("/api/users/**").hasAuthority("ADMIN")
//                .antMatchers(HttpMethod.GET, "/v2/api-docs/**", "/swagger-ui.html*", "/company/logo*").permitAll()
               .antMatchers("/api/**").authenticated()
               .anyRequest().authenticated()
               .and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(new CustomAccessDeniedHandler());
    }

}

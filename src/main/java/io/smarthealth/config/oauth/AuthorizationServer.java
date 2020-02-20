package io.smarthealth.config.oauth;
 
import io.smarthealth.security.service.CustomTokenEnhancer;
import io.smarthealth.security.service.CustomUserDetailsService;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 *
 * @author Kelsas
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;
    private final CustomUserDetailsService userDetailsService;
    private final CustomTokenEnhancer customTokenEnhancer;
    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Value("${jwt.clientId:smarthealth}")
    private String clientId;

    @Value("${jwt.client-secret:ccUyb6vS4S8nxfbKPCrN}")
    private String clientSecret;

    @Value("${jwt.accessTokenValidititySeconds:43200}") // 12 hours
    private int accessTokenValiditySeconds;

    @Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
    private String[] authorizedGrantTypes;

    @Value("${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
    private int refreshTokenValiditySeconds;

    public AuthorizationServer(DataSource dataSource, PasswordEncoder passwordEncoder, TokenStore tokenStore, CustomUserDetailsService userDetailsService,
            CustomTokenEnhancer customTokenEnhancer, JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.userDetailsService = userDetailsService;
        this.customTokenEnhancer = customTokenEnhancer;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .secret(passwordEncoder.encode(clientSecret))
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .authorizedGrantTypes(authorizedGrantTypes)
                .scopes("read", "write")
                .resourceIds("api");

    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .passwordEncoder(passwordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, jwtAccessTokenConverter));
        endpoints
                .tokenStore(tokenStore)
                .tokenEnhancer(enhancerChain)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .authorizationCodeServices(authorizationCodeServices())
                .reuseRefreshTokens(false);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
}

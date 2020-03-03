package io.smarthealth.config.oauth.delete;

//package io.smarthealth.config.oauth;
//
//import java.security.KeyPair;
//import javax.annotation.Resource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.env.Environment;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.approval.ApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
//import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
//import org.springframework.stereotype.Component;
//
///**
// *
// * @author Kelsas
// */
//@Component
//public class AuthenticationConfig {
// @Autowired
//    private ClientDetailsService clientDetailsService;
//
//    @Resource
//    private Environment env;
//
//    @Bean
//    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
//
//        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
//        handler.setTokenStore(tokenStore);
//        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
//        handler.setClientDetailsService(clientDetailsService);
//        return handler;
//    }
//
//    @Bean
//    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
//        TokenApprovalStore store = new TokenApprovalStore();
//        store.setTokenStore(tokenStore);
//        return store;
//    }
//
//    @Bean
//    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
//        return new JwtTokenStore(accessTokenConverter);
//    }
//
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter(KeyPair keyPair) {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setKeyPair(keyPair);
//        return converter;
//    }
//
//    @Bean
//    public KeyPair getKeyPair() {
//
//        KeyStoreKeyFactory keyStoreKeyFactory
//                = new KeyStoreKeyFactory(new ClassPathResource(env.getProperty("jwt.key-store")), env.getProperty("jwt.password").toCharArray());
//
//        return keyStoreKeyFactory.getKeyPair(env.getProperty("jwt.alias"));
//    }
//
//    @Bean
//    @Primary
//    public DefaultTokenServices tokenServices(TokenStore tokenStore) {
//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore);
//        defaultTokenServices.setSupportRefreshToken(true);
//        return defaultTokenServices;
//    }
//    
//
//}

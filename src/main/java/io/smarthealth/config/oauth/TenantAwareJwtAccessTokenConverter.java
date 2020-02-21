///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package io.smarthealth.config.oauth;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//
///**
// *
// * @author Kelsas
// */
//public class TenantAwareJwtAccessTokenConverter extends JwtAccessTokenConverter {
//
//    @Override
//    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//        ClientEntity clientEntity = getClientEntity(authentication);
//        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
//        info.putAll(clientEntity.getAdditionalInformationForToken()); // the additional information includes "tenant"="..."
//        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
//        customAccessToken.setAdditionalInformation(info);
//        return super.enhance(customAccessToken, authentication);
//    }
// private ClientEntity getClientEntity(OAuth2Authentication authentication) {
//    String clientId = (String) authentication.getPrincipal();
//    String tenant = TenantHeaderHelper.getTenantFromRequest();
//    return getClientEntityFromDatabase(clientId, tenant); // this includes some assertions to make sure the requested client exists
//}
//}

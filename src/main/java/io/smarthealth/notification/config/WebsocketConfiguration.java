/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.config;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final String IP_ADDRESS = "IP_ADDRESS";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws", "/websocket/tracker")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        Principal principal = request.getPrincipal();
                        if (principal == null) {
                            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            authorities.add(new SimpleGrantedAuthority("ANONYMOUS"));
                            principal = new AnonymousAuthenticationToken("WebsocketConfiguration", "anonymous", authorities);
                        }
                        return principal;
                    }
                })
                .setAllowedOrigins("*")
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor());

    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    attributes.put(IP_ADDRESS, servletRequest.getRemoteAddress());
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }
    
//    @EventListener
//    public void handleSubscribeEvent(SessionSubscribeEvent event) {
//        log.info("<==> handleSubscribeEvent: username="+event.getUser().getName()+", event="+event);
//    }
//
//    @EventListener
//    public void handleConnectEvent(SessionConnectEvent event) {
//        log.info("===> handleConnectEvent: username="+event.getUser().getName()+", event="+event);
//    }
//
//    @EventListener
//    public void handleDisconnectEvent(SessionDisconnectEvent event) {
//        log.info("<=== handleDisconnectEvent: username="+event.getUser().getName()+", event="+event);
//    }
}

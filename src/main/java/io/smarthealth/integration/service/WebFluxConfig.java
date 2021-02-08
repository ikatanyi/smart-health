/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.service;

/**
 *
 * @author Ikatanyi
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer
{  
	Logger logger = LoggerFactory.getLogger(WebFluxConfig.class);
	
	@Bean
	public WebClient getWebClient()
	{
		HttpClient httpClient = HttpClient.create()
		        .tcpConfiguration(client ->
		                client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
		                .doOnConnected(conn -> conn
		                        .addHandlerLast(new ReadTimeoutHandler(100))
		                        .addHandlerLast(new WriteTimeoutHandler(10))));
		
		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));	    

		return WebClient.builder()
		        .baseUrl("https://data.smartapplicationsgroup.com/api/smartlink/json")
		        .clientConnector(connector)
		        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .defaultHeader("X-GRAVITEE-API-KEY", "db066748-9bb3-430b-a94f-ac744685adf3")
		        .build();
	}
}

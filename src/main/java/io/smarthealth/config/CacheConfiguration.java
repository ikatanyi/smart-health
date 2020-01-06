package io.smarthealth.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;


/**
 * Cache configuration intended for caches providing the JCache API. This configuration creates the used cache for the
 * application and enables statistics that become accessible via JMX.
 */
@Configuration
@EnableCaching
class CacheConfiguration {



}

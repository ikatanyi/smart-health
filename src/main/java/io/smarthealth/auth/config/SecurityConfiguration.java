package io.smarthealth.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component 
@ConfigurationProperties(prefix = "smarthealth-security") 
public class SecurityConfiguration {
    private String webAppClientId;
    private String webAppClientSecret;
    private int accessTokenValidity;
    private int refreshTokenValidity;

    public String getWebAppClientId() {
        return webAppClientId;
    }

    public void setWebAppClientId(String webAppClientId) {
        this.webAppClientId = webAppClientId;
    }

    public String getWebAppClientSecret() {
        return webAppClientSecret;
    }

    public void setWebAppClientSecret(String webAppClientSecret) {
        this.webAppClientSecret = webAppClientSecret;
    }

    public int getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }
 
    
}

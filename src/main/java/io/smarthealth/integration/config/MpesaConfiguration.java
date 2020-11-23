package io.smarthealth.integration.config;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("safaricom")
public class MpesaConfiguration {

    private String shortCode;
    private String passKey;
    private String transactionType;
    private String baseUri;
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    private String callbackUrl;

    public String getPassword() {
        String originalString = shortCode.concat(passKey).concat(timestamp);
        return Base64.getEncoder().encodeToString(originalString.getBytes());
    }
}
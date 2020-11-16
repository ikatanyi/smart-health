/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
@Component
@ConfigurationProperties("smarthealth.mpesa")
public class Mpesa {

    public enum TransactionType {
        CustomerPayBillOnline,
        CustomerBuyGoodsOnline
    }
//    @Value("${smarthealth.mpesa.shortCode}")
    private String shortCode;
    
//    @Value("${smarthealth.mpesa.passKey}")
    private String passKey;
    
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    public String getPassword() {
        String originalString = shortCode.concat(passKey).concat(timestamp);
        return Base64.getEncoder().encodeToString(originalString.getBytes());
    }

}

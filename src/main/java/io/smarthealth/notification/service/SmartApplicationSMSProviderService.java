package io.smarthealth.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smarthealth.infrastructure.lang.UnsafeOkHttpClient;
import io.smarthealth.notification.domain.SMSConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Slf4j
public class SmartApplicationSMSProviderService {

    public String sendSingleSMS(String phone, String msg, SMSConfiguration smsConfiguration) {
        String status = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            String encMessage = URLEncoder.encode(msg, StandardCharsets.UTF_8.toString());
            String encPhone = URLEncoder.encode(phone.trim(), StandardCharsets.UTF_8.toString());
            String url = "https://data.smartapplicationsgroup.com/api/smsgateway/send?to=" + encPhone + "&message=" + encMessage;
            log.info("Sending SMS initiated ... ");
            Request request = new Request.Builder()
                    .url(url)
//                    .header("X-Gravitee-Api-Key", "86b45e1a-f22c-44d2-a434-a7c4a49f900a")
                    .header("X-Gravitee-Api-Key", smsConfiguration.getApiKey())
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            JsonNode rootNode = objectMapper.readTree(response.body().string());
            status = rootNode.path("AfricasTalkingResponse").path("SMSMessageData").path("Recipients").path(
                    "Recipient").path("status").textValue();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SmsMessagingService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(SmsMessagingService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return status;
    }
}

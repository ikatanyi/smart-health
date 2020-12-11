/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.messaging.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.messaging.service.EmailService;
import java.io.IOException;
import java.util.Locale;
import javax.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class SendingController {
    private final EmailService emailService;

    public SendingController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @PostMapping("/sendMailWithAttachment")
    public ResponseEntity<AResponse> sendMailWithAttachment(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            @RequestParam("attachment") final MultipartFile attachment,
            final Locale locale) throws MessagingException, IOException {

        this.emailService.sendMailWithAttachment(
                recipientName, recipientEmail, attachment.getOriginalFilename(),
                attachment.getBytes(), attachment.getContentType(), locale);
        return ResponseEntity.ok(new AResponse("Success", "Email Sent for Delivery"));

    }

    @Data
    @AllArgsConstructor
    public class AResponse {
//203463600580
        private String status;
        private String message;
    }
}
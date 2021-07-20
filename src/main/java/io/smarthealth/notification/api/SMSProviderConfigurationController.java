package io.smarthealth.notification.api;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notification.data.NotificationResponse;
import io.smarthealth.notification.data.SMSConfigurationData;
import io.smarthealth.notification.data.SmsMessageData;
import io.smarthealth.notification.domain.SMSConfiguration;
import io.smarthealth.notification.service.SMSConfigurationService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SMSProviderConfigurationController {
    private final SMSConfigurationService smsConfigurationService;

    @PostMapping("/sms-configuration")
    @ResponseBody
    public ResponseEntity<SMSConfigurationData> save(@RequestBody @Valid SMSConfigurationData data) {
        SMSConfigurationData smsConfigurationData =
                SMSConfigurationData.map(smsConfigurationService.saveConfiguration(data));
        return ResponseEntity.status(HttpStatus.CREATED).body(smsConfigurationData);
    }

    @GetMapping("/sms-configuration")
    public ResponseEntity<List<SMSConfigurationData>> findAll() {
        List<SMSConfigurationData> smsConfigurationData =
                smsConfigurationService.findAll().stream().map(c -> SMSConfigurationData.map(c)).collect(Collectors.toList());
        return ResponseEntity.ok(smsConfigurationData);
    }

    @PutMapping("/sms-configuration/{id}")
    public ResponseEntity<SMSConfigurationData> updateSmsprovider(
            @RequestBody SMSConfigurationData smsConfigurationData,
            @PathVariable("id") Long id) {
        smsConfigurationService.findById(id);

        SMSConfiguration e = smsConfigurationService.updateSmsprovider(smsConfigurationData, id);

        return ResponseEntity.ok(SMSConfigurationData.map(e));
    }


}

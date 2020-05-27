/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.service;

import io.smarthealth.clinical.record.service.DoctorRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class NotifyService {

    private final DoctorRequestService requestService;
    private final MessageSendingOperations<String> messagingTemplate;

    //can convert this to event and register event here when 
//    @Scheduled(fixedDelay = 10000)
    public void sendDoctorRequest() {

        messagingTemplate.convertAndSend("/topic/requests", requestService.getUnfilledDoctorRequests(null));
    }

}

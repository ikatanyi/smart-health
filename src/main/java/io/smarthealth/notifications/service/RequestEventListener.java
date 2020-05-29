package io.smarthealth.notifications.service;

import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.notifications.data.RequestCreatedEvent;
import io.smarthealth.notifications.data.RequestUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RequestEventListener {//implements ApplicationListener<RequestCreatedEvent> {

    private final MessageSendingOperations<String> messagingTemplate;
    private final DoctorRequestService requestService;

    @Async
//    @EventListener
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMPLETION)
    public void handleRequestedUpdatedEvent(RequestUpdatedEvent e) {
        messagingTemplate.convertAndSend("/topic/requests." + e.getRequestType().name(), requestService.getUnfilledDoctorRequests(e.getRequestType()));        
    }
    
    @Async
    @EventListener
    public void handleCreatedUpdatedEvent(RequestCreatedEvent e) {
        e.getRequestType().forEach((type) -> {
            messagingTemplate.convertAndSend("/topic/requests." + type.name(), requestService.getUnfilledDoctorRequests(type));
        }); 
    }
    
//     @EventListener(condition = "#event.name eq 'reflectoring'")
//  void handleConditionalListener(UserRemovedEvent event) {
//    // handle UserRemovedEvent
//  }
}

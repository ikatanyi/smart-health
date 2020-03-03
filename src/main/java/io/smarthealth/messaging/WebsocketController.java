package io.smarthealth.messaging;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Controller
public class WebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebsocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/greetings")
    public void greet(String greeting) {
        String text = "[" + Instant.now() + "]: " + greeting;
        this.simpMessagingTemplate.convertAndSend("/topic/greetings", text);
    }
}
